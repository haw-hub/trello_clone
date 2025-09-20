package com.spring.repository;

import com.spring.enums.AddMemberResult;
import com.spring.model.*;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TrelloRepo {

    @PersistenceContext
    private EntityManager em;

    // ======================
    // User operations
    // ======================
    public UserBean login(String username, String password) {
        try {
            return em.createQuery(
                    "SELECT u FROM UserBean u WHERE u.username = :username AND u.password = :password",
                    UserBean.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UserBean getUserByEmail(String email) {
        try {
            return em.createQuery(
                    "SELECT u FROM UserBean u WHERE u.email = :email",
                    UserBean.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void saveUser(UserBean user) {
        em.persist(user);
    }


    // ======================
    // Workspace operations
    // ======================
    public boolean createWorkspaceWithCreator(WorkspaceBean workspace) {
        try {
            em.persist(workspace);
            em.flush(); // ensure workspace.getId() is available

            // Load managed references
            UserBean creator = em.getReference(UserBean.class, workspace.getCreated_by());
            WorkspaceBean managedWs = em.getReference(WorkspaceBean.class, workspace.getId());

            WorkspaceMemberId memberId = new WorkspaceMemberId(
                workspace.getCreated_by(),
                workspace.getId()
            );

            WorkspaceMemberBean member = new WorkspaceMemberBean();
            member.setId(memberId);
            member.setRole("OWNER");
            member.setUser(creator);        // REQUIRED due to @MapsId("usersId")
            member.setWorkspace(managedWs); // REQUIRED due to @MapsId("workspacesId")

            em.persist(member);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create workspace + owner membership", e);
        }
    }
    
    public boolean updateWorkspace(int workspaceId, String name, String description) {
        try {
            WorkspaceBean ws = em.find(WorkspaceBean.class, workspaceId);
            if (ws == null) return false;

            ws.setName(name);
            ws.setDescription(description);

            em.merge(ws);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    public boolean deleteWorkspace(int workspaceId) {
        try {
            // 1. Delete all card children (comments, attachments, checklists, members)
            em.createQuery("DELETE FROM CommentBean c WHERE c.card.list.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            em.createQuery("DELETE FROM AttachmentBean a WHERE a.card.list.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            em.createQuery("DELETE FROM ChecklistBean ch WHERE ch.card.list.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            em.createQuery("DELETE FROM CardMemberBean cm WHERE cm.card.list.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            // 2. Delete all cards
            em.createQuery("DELETE FROM CardBean c WHERE c.list.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            // 3. Delete all lists
            em.createQuery("DELETE FROM ListBean l WHERE l.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            // 4. Delete all board members (avoid FK errors)
            em.createQuery("DELETE FROM BoardMemberBean bm WHERE bm.board.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            // 5. Delete all boards
            em.createQuery("DELETE FROM BoardBean b WHERE b.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            // 6. Delete workspace members
            em.createQuery("DELETE FROM WorkspaceMemberBean wm WHERE wm.workspace.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            // 7. Finally delete the workspace
            em.createQuery("DELETE FROM WorkspaceBean w WHERE w.id = :wsId")
              .setParameter("wsId", workspaceId)
              .executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public List<WorkspaceBean> getWorkspacesByUserId(int userId) {
        return em.createQuery(
            "SELECT DISTINCT w FROM WorkspaceBean w " +
            "LEFT JOIN FETCH w.members m " +
            "LEFT JOIN FETCH w.createdByUser " +
            "WHERE w.created_by = :userId OR (m.user IS NOT NULL AND m.user.id = :userId) " +
            "ORDER BY w.name ASC", 
            WorkspaceBean.class
        )
        .setParameter("userId", userId)
        .getResultList();
    }

    public List<Object[]> getWorkspacesWithRolesByUserId(int userId) {
        return em.createQuery(
            "SELECT w, wm.role FROM WorkspaceBean w " +
            "LEFT JOIN w.members wm ON wm.user.id = :userId " +
            "WHERE w.created_by = :userId OR wm.user.id = :userId " +
            "ORDER BY w.name ASC", 
            Object[].class
        )
        .setParameter("userId", userId)
        .getResultList();
    }

    public WorkspaceBean getWorkspaceById(int workspaceId) {
        try {
            return em.find(WorkspaceBean.class, workspaceId);
        } catch (Exception e) {
            return null;
        }
    }

    // ======================
    // Workspace Member operations
    // ======================
    public WorkspaceMemberBean getWorkspaceMembership(int workspaceId, int userId) {
        return em.createQuery(
            "SELECT wm FROM WorkspaceMemberBean wm " +
            "WHERE wm.workspace.id = :workspaceId AND wm.user.id = :userId",
            WorkspaceMemberBean.class
        )
        .setParameter("workspaceId", workspaceId)
        .setParameter("userId", userId)
        .getSingleResult();
    }
    
    

    public boolean addWorkspaceMember(int workspaceId, int userId, String role) {
        try {
            // Check if already member
            Long count = em.createQuery(
                "SELECT COUNT(wm) FROM WorkspaceMemberBean wm " +
                "WHERE wm.workspace.id = :workspaceId AND wm.user.id = :userId",
                Long.class
            )
            .setParameter("workspaceId", workspaceId)
            .setParameter("userId", userId)
            .getSingleResult();

            if (count > 0) return false;

            WorkspaceMemberId memberId = new WorkspaceMemberId(userId, workspaceId);

            WorkspaceMemberBean member = new WorkspaceMemberBean();
            member.setId(memberId);
            member.setRole(role);
            member.setUser(em.find(UserBean.class, userId));
            member.setWorkspace(em.find(WorkspaceBean.class, workspaceId));

            em.persist(member);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeWorkspaceMember(int workspaceId, int userId) {
        try {
            int deleted = em.createQuery(
                "DELETE FROM WorkspaceMemberBean wm " +
                "WHERE wm.workspace.id = :workspaceId AND wm.user.id = :userId"
            )
            .setParameter("workspaceId", workspaceId)
            .setParameter("userId", userId)
            .executeUpdate();
            return deleted > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<UserBean> getWorkspaceMembers(int workspaceId) {
        return em.createQuery(
            "SELECT wm.user FROM WorkspaceMemberBean wm " +
            "WHERE wm.workspace.id = :workspaceId",
            UserBean.class
        )
        .setParameter("workspaceId", workspaceId)
        .getResultList();
    }
    
    public List<WorkspaceMemberBean> getWorkspaceMembersWithRole(int workspaceId) {
        return em.createQuery(
            "SELECT wm FROM WorkspaceMemberBean wm " +
            "JOIN FETCH wm.user " +
            "WHERE wm.workspace.id = :workspaceId",
            WorkspaceMemberBean.class
        )
        .setParameter("workspaceId", workspaceId)
        .getResultList();
    }


    public boolean updateWorkspaceMemberRole(int workspaceId, int userId, String newRole) {
        try {
            int updated = em.createQuery(
                "UPDATE WorkspaceMemberBean wm SET wm.role = :role " +
                "WHERE wm.workspace.id = :workspaceId AND wm.user.id = :userId"
            )
            .setParameter("role", newRole)
            .setParameter("workspaceId", workspaceId)
            .setParameter("userId", userId)
            .executeUpdate();
            return updated > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ======================
    // Board operations
    // ======================
    @Transactional
    public boolean createBoard(BoardBean board, int creatorUserId) {
        try {
            em.persist(board);

            // add the creator as a board member
            BoardMemberBean member = new BoardMemberBean();
            member.setBoard(board);
            member.setUser(em.find(UserBean.class, creatorUserId));
            member.setRole("OWNER");

            em.persist(member);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    @Transactional
    public boolean deleteBoardById(int boardId) {
        try {
            // 1. Delete card-related entities (comments, attachments, checklists, members)
            em.createQuery("DELETE FROM CommentBean c WHERE c.card.list.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            em.createQuery("DELETE FROM AttachmentBean a WHERE a.card.list.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            em.createQuery("DELETE FROM ChecklistBean ch WHERE ch.card.list.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            em.createQuery("DELETE FROM CardMemberBean cm WHERE cm.card.list.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            // 2. Delete cards
            em.createQuery("DELETE FROM CardBean c WHERE c.list.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            // 3. Delete lists
            em.createQuery("DELETE FROM ListBean l WHERE l.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            // 4. Delete board members
            em.createQuery("DELETE FROM BoardMemberBean bm WHERE bm.board.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            // 5. Finally delete board itself
            em.createQuery("DELETE FROM BoardBean b WHERE b.id = :boardId")
              .setParameter("boardId", boardId)
              .executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBoard(int boardId, String title, String background) {
        try {
            if (background != null && !background.trim().isEmpty()) {
                em.createQuery("UPDATE BoardBean b SET b.title = :title, b.background = :background WHERE b.id = :boardId")
                  .setParameter("title", title)
                  .setParameter("background", background)
                  .setParameter("boardId", boardId)
                  .executeUpdate();
            } else {
                em.createQuery("UPDATE BoardBean b SET b.title = :title WHERE b.id = :boardId")
                  .setParameter("title", title)
                  .setParameter("boardId", boardId)
                  .executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public BoardBean getBoardById(int boardId) {
        try {
            return em.find(BoardBean.class, boardId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<BoardBean> getBoardsByWorkspaceId(int workspaceId) {
        return em.createQuery(
            "SELECT b FROM BoardBean b WHERE b.workspace.id = :workspaceId",
            BoardBean.class
        )
        .setParameter("workspaceId", workspaceId)
        .getResultList();
    }
    
 // ✅ Update only background color
    public boolean updateBackground(int boardId, String background) {
        try {
            BoardBean board = em.find(BoardBean.class, boardId);
            if (board != null) {
                board.setBackground(background);
                em.merge(board);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

 // ======================
 // Board member operations
 // ======================
    
    
    
    public AddMemberResult addMemberToBoard(int boardId, int userId, String role) {
        try {
            BoardBean board = em.find(BoardBean.class, boardId);
            UserBean user = em.find(UserBean.class, userId);
            if (board == null || user == null) return AddMemberResult.NOT_FOUND;

            Long count = em.createQuery(
                "SELECT COUNT(bm) FROM BoardMemberBean bm WHERE bm.board.id = :boardId AND bm.user.id = :userId",
                Long.class
            )
            .setParameter("boardId", boardId)
            .setParameter("userId", userId)
            .getSingleResult();

            if (count > 0) {
                return AddMemberResult.DUPLICATE;
            }

            BoardMemberBean bm = new BoardMemberBean();
            bm.setBoard(board);
            bm.setUser(user);
            bm.setRole((role != null && !role.isBlank()) ? role : "member");

            em.persist(bm);
            return AddMemberResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return AddMemberResult.ERROR;
        }
    }


 
 public void updateBoardMemberRole(int boardId, int userId, String newRole) {
	    em.createQuery(
	        "UPDATE BoardMemberBean bm " +
	        "SET bm.role = :role " +
	        "WHERE bm.board.id = :boardId AND bm.user.id = :userId"
	    )
	    .setParameter("role", newRole)
	    .setParameter("boardId", boardId)
	    .setParameter("userId", userId)
	    .executeUpdate();
	}


 public boolean removeMemberFromBoard(int boardId, int userId) {
     try {
         BoardMemberBean bm = em.createQuery(
             "SELECT bm FROM BoardMemberBean bm WHERE bm.board.id = :boardId AND bm.user.id = :userId",
             BoardMemberBean.class
         )
         .setParameter("boardId", boardId)
         .setParameter("userId", userId)
         .getSingleResult();

         em.remove(bm);
         return true;
     } catch (Exception e) {
         return false;
     }
 }

 public List<BoardMemberBean> getBoardMembersWithRole(int boardId) {
	    return em.createQuery(
	        "SELECT bm FROM BoardMemberBean bm " +
	        "JOIN FETCH bm.user " +
	        "WHERE bm.board.id = :boardId",
	        BoardMemberBean.class
	    )
	    .setParameter("boardId", boardId)
	    .getResultList();
	}


    // ======================
    // List operations
    // ======================
 @Transactional
 public boolean addList(ListBean list, int boardId) {
     try {
         // 1. Fetch parent board
         BoardBean board = em.find(BoardBean.class, boardId);
         if (board == null) {
             throw new IllegalArgumentException("Board with ID " + boardId + " not found");
         }

         // 2. Attach both entity and id
         list.setBoard(board);
         list.setBoards_id(board.getId());  // ensure board_id column is filled

         // 3. Persist list
         em.persist(list);
         return true;
     } catch (Exception e) {
         e.printStackTrace();
         return false;
     }
 }

    
    public boolean updateListTitle(int listId, String newTitle) {
        try {
            ListBean list = em.find(ListBean.class, listId);
            if (list == null) return false;

            list.setTitle(newTitle);
            em.merge(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

   


    public List<ListBean> getListsByBoardId(int boardId) {
        return em.createQuery(
            "SELECT l FROM ListBean l WHERE l.board.id = :boardId ORDER BY l.position ASC",
            ListBean.class
        )
        .setParameter("boardId", boardId)
        .getResultList();
    }

    public int getNextListPosition(int boardId) {
        try {
            Integer maxPos = em.createQuery(
                "SELECT MAX(l.position) FROM ListBean l WHERE l.board.id = :boardId",
                Integer.class
            )
            .setParameter("boardId", boardId)
            .getSingleResult();
            return maxPos != null ? maxPos + 1 : 1;
        } catch (Exception e) {
            return 1;
        }
    }
    
    @Transactional
    public boolean deleteList(int listId) {
        try {
            // 1. Delete card-related entities (comments, attachments, checklists, members)
            em.createQuery("DELETE FROM CommentBean c WHERE c.card.list.id = :listId")
              .setParameter("listId", listId)
              .executeUpdate();

            em.createQuery("DELETE FROM AttachmentBean a WHERE a.card.list.id = :listId")
              .setParameter("listId", listId)
              .executeUpdate();

            em.createQuery("DELETE FROM ChecklistBean ch WHERE ch.card.list.id = :listId")
              .setParameter("listId", listId)
              .executeUpdate();

            em.createQuery("DELETE FROM CardMemberBean cm WHERE cm.card.list.id = :listId")
              .setParameter("listId", listId)
              .executeUpdate();

            // 2. Delete the cards
            em.createQuery("DELETE FROM CardBean c WHERE c.list.id = :listId")
              .setParameter("listId", listId)
              .executeUpdate();

            // 3. Finally delete the list itself
            em.createQuery("DELETE FROM ListBean l WHERE l.id = :listId")
              .setParameter("listId", listId)
              .executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // ======================
    // Card operations
    // ======================
    
    public ListBean getListById(int listId) {
        try {
            return em.find(ListBean.class, listId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public UserBean getUserById(int userId) {
        return em.find(UserBean.class, userId);
    }
    
    @Transactional
    public boolean addCard(CardBean card, int listId) {
        try {
            ListBean list = em.find(ListBean.class, listId);
            if (list == null) {
                throw new IllegalArgumentException("List with ID " + listId + " not found");
            }

            card.setList(list); // ✅ will now populate lists_id automatically
            em.persist(card);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    @Transactional
    public boolean updateCard(int cardId, String title, String description) {
        CardBean card = em.find(CardBean.class, cardId);
        if (card == null) return false;

        card.setTitle(title);
        card.setDescription(description);
        em.merge(card);
        return true;
    }

    
    public Integer getBoardIdByListId(int listId) {
        try {
            return em.createQuery(
                "SELECT l.board.id FROM ListBean l WHERE l.id = :listId",
                Integer.class
            ).setParameter("listId", listId)
             .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public List<CardBean> getCardsByListId(int listId) {
        return em.createQuery(
            "SELECT c FROM CardBean c WHERE c.list.id = :listId ORDER BY c.position ASC",
            CardBean.class
        )
        .setParameter("listId", listId)
        .getResultList();
    }

    public int getNextCardPosition(int listId) {
        try {
            Integer maxPos = em.createQuery(
                "SELECT MAX(c.position) FROM CardBean c WHERE c.list.id = :listId",
                Integer.class
            )
            .setParameter("listId", listId)
            .getSingleResult();
            return maxPos != null ? maxPos + 1 : 1;
        } catch (Exception e) {
            return 1;
        }
    }
    
    public Integer getBoardIdByCardId(int cardId) {
        try {
            return em.createQuery(
                "SELECT l.board.id FROM CardBean c JOIN c.list l WHERE c.id = :cardId",
                Integer.class
            ).setParameter("cardId", cardId)
             .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    //=================card member=======================
    

 // In TrelloRepo.java - Replace card member methods

    @Transactional
    public boolean assignMemberToCard(int cardId, int userId) {
        try {
            // Check if already assigned
            Long count = em.createQuery(
                "SELECT COUNT(cm) FROM CardMemberBean cm " +
                "WHERE cm.cardId = :cardId AND cm.userId = :userId", 
                Long.class)
                .setParameter("cardId", cardId)
                .setParameter("userId", userId)
                .getSingleResult();

            if (count > 0) {
                return true; // Already exists
            }

            // Create new card member relationship
            CardMemberBean cardMember = new CardMemberBean();
            cardMember.setCardId(cardId);
            cardMember.setUserId(userId);
            cardMember.setAssignedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            
            em.persist(cardMember);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean removeMemberFromCard(int cardId, int userId) {
        try {
            int deleted = em.createQuery(
                "DELETE FROM CardMemberBean cm " +
                "WHERE cm.cardId = :cardId AND cm.userId = :userId")
                .setParameter("cardId", cardId)
                .setParameter("userId", userId)
                .executeUpdate();
            return deleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public List<UserBean> getCardMembers(int cardId) {
        try {
            return em.createQuery(
                "SELECT u FROM UserBean u " +
                "WHERE u.id IN (SELECT cm.userId FROM CardMemberBean cm WHERE cm.cardId = :cardId)", 
                UserBean.class)
                .setParameter("cardId", cardId)
                .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void saveCard(CardBean card) {
        try {
            if (card.getId() == null) {
                em.persist(card);
            } else {
                em.merge(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Additional method to get card members with details
    @Transactional
    public List<CardMemberBean> getCardMembersWithDetails(int cardId) {
        try {
            return em.createQuery(
                "SELECT cm FROM CardMemberBean cm " +
                "JOIN FETCH cm.user " +
                "WHERE cm.cardId = :cardId", 
                CardMemberBean.class)
                .setParameter("cardId", cardId)
                .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    //======================== card activity =====================
    
    public void logCardActivity(int cardId, int userId, String action) {
        CardBean card = em.find(CardBean.class, cardId);
        UserBean user = em.find(UserBean.class, userId);

        if (card != null && user != null) {
            CardActivityBean activity = new CardActivityBean();
            activity.setCard(card);
            activity.setBoard(card.getList().getBoard()); // ✅ auto-fill board_id

            activity.setUser(user);
            activity.setAction(action);
            activity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            em.persist(activity);
        }
    }
    
    @Transactional
    public void logBoardActivity(int boardId, int userId, String action) {
        BoardBean board = em.find(BoardBean.class, boardId);
        UserBean user = em.find(UserBean.class, userId);

        if (board == null || user == null) {
            throw new IllegalArgumentException("Invalid board or user ID");
        }

        CardActivityBean activity = new CardActivityBean();
        activity.setBoard(board); // ✅ Set board reference
        activity.setUser(user);
        activity.setAction(action);
        activity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        em.persist(activity);

    }
    
    public List<CardActivityBean> getBoardActivities(int boardId) {
        return em.createQuery(
            "SELECT ca FROM CardActivityBean ca " +
            "JOIN FETCH ca.user u " +          // ✅ fetch user
            "JOIN FETCH ca.board b " +         // ✅ fetch board (optional)
            "LEFT JOIN FETCH ca.card c " +     // ✅ fetch card (optional)
            "WHERE b.id = :boardId " +
            "ORDER BY ca.createdAt DESC",
            CardActivityBean.class
        )
        .setParameter("boardId", boardId)
        .getResultList();
    }



    
    



    public List<CardActivityBean> getCardActivities(int cardId) {
        return em.createQuery(
            "SELECT ca FROM CardActivityBean ca WHERE ca.card.id = :cardId ORDER BY ca.createdAt DESC",
            CardActivityBean.class
        ).setParameter("cardId", cardId).getResultList();
    }

    
    
    //========================due date===========================
    
    public boolean updateDueDate(int cardId, java.sql.Date dueDate) {
        try {
            CardBean card = em.find(CardBean.class, cardId);
            if (card != null) {
                card.setDue_date(dueDate);
                em.merge(card);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ======================
    // Attachment operations
    // ======================
 // Fix the uploadAttachment method in TrelloRepo.java
    @Transactional
    public boolean uploadAttachment(int cardId, int userId, MultipartFile file) {
        try {
            CardBean card = em.find(CardBean.class, cardId);
            UserBean user = em.find(UserBean.class, userId);

            if (card == null || user == null || file.isEmpty()) {
                return false;
            }

            AttachmentBean attachment = new AttachmentBean();
            attachment.setCard(card); // Use relationship
            attachment.setUser(user); // Use relationship
            attachment.setFilename(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFile(file.getBytes());
            attachment.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            em.persist(attachment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AttachmentBean> getAttachmentsByCardId(int cardId) {
        return em.createQuery(
            "SELECT a FROM AttachmentBean a WHERE a.card.id = :cardId",
            AttachmentBean.class
        )
        .setParameter("cardId", cardId)
        .getResultList();
    }
    
    
    public AttachmentBean getAttachmentById(int id) {
        try {
            return em.find(AttachmentBean.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    // ======================
    // Comment operations
    // ======================
    public boolean addCardComment(int cardId, int userId, String content) {
        try {
            CommentBean comment = new CommentBean();
            comment.setContent(content);
            comment.setCards_id(cardId);
            comment.setUsers_id(userId);
            comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // ✅ fetch and set the actual user entity
            UserBean user = em.find(UserBean.class, userId);
            if (user != null) {
                comment.setUser(user);
            }

            // ✅ fetch and set the card entity as well (good practice for bidirectional mapping)
            CardBean card = em.find(CardBean.class, cardId);
            if (card != null) {
                comment.setCard(card);
            }

            em.persist(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public List<CommentBean> getCommentsByCardId(int cardId) {
        return em.createQuery(
                "SELECT c FROM CommentBean c " +
                "JOIN FETCH c.user " +
                "WHERE c.card.id = :cardId " +
                "ORDER BY c.createdAt DESC",
                CommentBean.class
        )
        .setParameter("cardId", cardId)
        .getResultList();
    }
    
   

    // ======================
    // Checklist operations
    // ======================
    public boolean addChecklist(int cardId, String title) {
        try {
            ChecklistBean checklist = new ChecklistBean();
            checklist.setTitle(title);
            checklist.setCardsId(cardId);
            checklist.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            em.persist(checklist);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getChecklistTitleById(int checklistId) {
        try {
            ChecklistBean checklist = em.find(ChecklistBean.class, checklistId);
            return (checklist != null) ? checklist.getTitle() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean addChecklistItem(int checklistId, String text) {
        try {
            ChecklistItemBean item = new ChecklistItemBean();
            item.setText(text);
            item.setChecklistsId(checklistId);
            item.setCompleted(false);
            item.setPosition(getNextPositionForChecklistItem(checklistId));
            
            em.persist(item);
            em.flush(); // Force immediate write to database
            em.refresh(item); // Refresh the entity from database
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateChecklist(int checklistId, String title) {
        try {
            ChecklistBean checklist = em.find(ChecklistBean.class, checklistId);
            if (checklist != null) {
                checklist.setTitle(title);
                em.merge(checklist);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteChecklist(int checklistId) {
        try {
            ChecklistBean checklist = em.find(ChecklistBean.class, checklistId);
            if (checklist != null) {
                em.remove(checklist);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateChecklistItem(int itemId, String text) {
        try {
            ChecklistItemBean item = em.find(ChecklistItemBean.class, itemId);
            if (item != null) {
                item.setText(text);
                em.merge(item);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleChecklistItem(int itemId,String text) {
        try {
            ChecklistItemBean item = em.find(ChecklistItemBean.class, itemId);
            if (item != null) {
                item.setCompleted(!item.isCompleted());
                item.setText(text);
                em.merge(item);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteChecklistItem(int itemId) {
        try {
            ChecklistItemBean item = em.find(ChecklistItemBean.class, itemId);
            if (item != null) {
                em.remove(item);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ChecklistItemBean> getChecklistItemsByChecklistId(int checklistId) {
        return em.createQuery(
            "SELECT i FROM ChecklistItemBean i WHERE i.checklistsId = :checklistId ORDER BY i.position",
            ChecklistItemBean.class
        )
        .setParameter("checklistId", checklistId)
        .getResultList();
    }

    public int getNextPositionForChecklistItem(int checklistId) {
        try {
            Integer maxPosition = em.createQuery(
                "SELECT MAX(i.position) FROM ChecklistItemBean i WHERE i.checklistsId = :checklistId", 
                Integer.class
            )
            .setParameter("checklistId", checklistId)
            .getSingleResult();
            
            return (maxPosition != null) ? maxPosition + 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

 // Fix the getChecklistsByCardId method
    public List<ChecklistBean> getChecklistsByCardId(int cardId) {
        return em.createQuery(
            "SELECT c FROM ChecklistBean c WHERE c.cardsId = :cardId ORDER BY c.position",
            ChecklistBean.class
        )
        .setParameter("cardId", cardId)
        .getResultList();
    }

    // Fix the getNextChecklistPosition method  
    public int getNextChecklistPosition(int cardId) {
        try {
            Integer maxPos = em.createQuery(
                "SELECT MAX(c.position) FROM ChecklistBean c WHERE c.cardsId = :cardId",
                Integer.class
            )
            .setParameter("cardId", cardId)
            .getSingleResult();
            return maxPos != null ? maxPos + 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // Fix the checklistExists method
    public boolean checklistExists(int cardId, String title) {
        try {
            Long count = em.createQuery(
                "SELECT COUNT(c) FROM ChecklistBean c WHERE c.cardsId = :cardId AND c.title = :title",
                Long.class
            )
            .setParameter("cardId", cardId)
            .setParameter("title", title)
            .getSingleResult();
            
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public Integer getCardIdByChecklistId(int checklistId) {
        try {
            return em.createQuery(
                "SELECT c.cardsId FROM ChecklistBean c WHERE c.id = :checklistId", 
                Integer.class
            )
            .setParameter("checklistId", checklistId)
            .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardBean getCardById(int cardId) {
        CardBean card = em.find(CardBean.class, cardId);
        if (card == null) {
            System.out.println("⚠ No card found with ID: " + cardId);
            return null;
        }
        return card;
    }

   

    public ChecklistItemBean getChecklistItemById(int id) {
        try {
            return em.find(ChecklistItemBean.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateChecklistItem(ChecklistItemBean item) {
        try {
            em.merge(item);
        } catch (Exception e) {
            throw new RuntimeException("Error updating checklist item", e);
        }
    }
    
    //=============drap and drop======================
    
    @Transactional
    public void updateCardPosition(int cardId, int position) {
        CardBean card = em.find(CardBean.class, cardId);
        if (card != null) {
            card.setPosition(position);
            em.merge(card);
        }
    }
    
    //=============== see assignment =====================
    
    @Transactional
    public List<CardBean> getCardsAssignedToUser(int userId) {
        try {
            return em.createQuery(
                "SELECT c "
                + "FROM CardBean c "
                + "JOIN c.cardMembers cm "
                + "WHERE cm.user.id = :userId"
                + "", CardBean.class)
                .setParameter("userId", userId)
                .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}

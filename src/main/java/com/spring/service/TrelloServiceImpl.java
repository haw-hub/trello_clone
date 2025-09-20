package com.spring.service;

import com.spring.enums.AddMemberResult;
import com.spring.model.*;
import com.spring.repository.TrelloRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TrelloServiceImpl implements TrelloService {

    @Autowired
    private TrelloRepo trelloRepo;

    // ===================== User =====================
    @Override
    public UserBean login(String username, String password) {
        return trelloRepo.login(username, password);
    }

    @Override
    public UserBean getUserByEmail(String email) {
        return trelloRepo.getUserByEmail(email);
    }

    @Override
    public void registerUser(UserBean user) {
        trelloRepo.saveUser(user);
    }

    // ===================== Workspaces =====================
    @Override
    public boolean createWorkspaceWithCreator(WorkspaceBean workspace) {
        return trelloRepo.createWorkspaceWithCreator(workspace);
    }

    @Override
    public List<WorkspaceBean> getUserWorkspaces(int userId) {
        return trelloRepo.getWorkspacesByUserId(userId);
    }
    
    @Override
    public boolean updateWorkspace(int workspaceId, String name, String description) {
        return trelloRepo.updateWorkspace(workspaceId, name, description);
    }

    @Override
    public boolean deleteWorkspace(int workspaceId) {
        return trelloRepo.deleteWorkspace(workspaceId);
    }

    
    @Override
    public List<Object[]> getWorkspacesWithRolesByUserId(int userId) {
        return trelloRepo.getWorkspacesWithRolesByUserId(userId);
    }
    
    @Override
    public WorkspaceBean getWorkspaceById(int workspaceId) {
        return trelloRepo.getWorkspaceById(workspaceId);
    }

    @Override
    public WorkspaceMemberBean getWorkspaceMembership(int workspaceId, int userId) {
        return trelloRepo.getWorkspaceMembership(workspaceId, userId);
    }

    @Override
    public List<UserBean> getWorkspaceMembers(int workspaceId) {
        return trelloRepo.getWorkspaceMembers(workspaceId);
    }

    @Override
    public boolean addWorkspaceMember(int workspaceId, int userId, String role) {
        return trelloRepo.addWorkspaceMember(workspaceId, userId, role);
    }

    @Override
    public boolean removeWorkspaceMember(int workspaceId, int userId) {
        return trelloRepo.removeWorkspaceMember(workspaceId, userId);
    }

    @Override
    public boolean updateWorkspaceMemberRole(int workspaceId, int userId, String role) {
        return trelloRepo.updateWorkspaceMemberRole(workspaceId, userId, role);
    }
    
    @Override
    public List<WorkspaceMemberBean> getWorkspaceMembersWithRole(int workspaceId) {
        return trelloRepo.getWorkspaceMembersWithRole(workspaceId);
    }


    // ===================== Boards =====================
    
    @Override
    public boolean updateBoardBackground(int boardId, String background) {
        return trelloRepo.updateBackground(boardId, background);
    }
    
    @Override
    public List<CardActivityBean> getBoardActivities(int boardId) {
        return trelloRepo.getBoardActivities(boardId);
    }
    
    @Override
    public boolean createBoard(BoardBean board, int creatorUserId) {
        return trelloRepo.createBoard(board, creatorUserId);
    }

    @Override
    public void removeBoard(int boardId) {
        trelloRepo.deleteBoardById(boardId);
    }
    
    @Override
    public boolean updateBoard(int boardId, String title, String background) {
        return trelloRepo.updateBoard(boardId, title, background);
    }

    
    @Override
    public BoardBean getBoardById(int boardId) {
        return trelloRepo.getBoardById(boardId);
    }

    @Override
    public List<BoardBean> getBoardsByWorkspaceId(int workspaceId) {
        return trelloRepo.getBoardsByWorkspaceId(workspaceId);
    }

    @Override
    public AddMemberResult addMemberToBoard(int boardId, int userId, String role) {
        return trelloRepo.addMemberToBoard(boardId, userId, role);
    }

    @Override
    public boolean removeMemberFromBoard(int boardId, int userId) {
        return trelloRepo.removeMemberFromBoard(boardId, userId);
    }

    @Override
    public List<BoardMemberBean> getBoardMembersWithRole(int boardId) {
        return trelloRepo.getBoardMembersWithRole(boardId);
    }

    @Override
    public void updateBoardMemberRole(int boardId, int userId, String newRole) {
        trelloRepo.updateBoardMemberRole(boardId, userId, newRole);
    }

    @Override
    public CardBean getCardById(int cardId) {
        return trelloRepo.getCardById(cardId);
    }



    // ===================== Lists =====================
    @Override
    public boolean addList(ListBean list, int boardId) {
        return trelloRepo.addList(list, boardId);
    }

    @Override
    public List<ListBean> getListsByBoardId(int boardId) {
        return trelloRepo.getListsByBoardId(boardId);
    }

    @Override
    public int getNextListPosition(int boardId) {
        return trelloRepo.getNextListPosition(boardId);
    }

    @Override
    
    public boolean updateListTitle(int listId, String newTitle) {
        return trelloRepo.updateListTitle(listId, newTitle);
    }

    @Override
    public boolean deleteList(int listId) {
        return trelloRepo.deleteList(listId);
    }

    
    // ===================== Cards =====================
    @Override
    public boolean addCard(CardBean card, int listId) {
        return trelloRepo.addCard(card, listId);
    }

    @Override
    public ListBean getListById(int listId) {
        return trelloRepo.getListById(listId);
    }

    
    @Override
    public boolean updateCard(int cardId, String title, String description) {
        return trelloRepo.updateCard(cardId, title, description);
    }


    @Override
    public List<CardBean> getCardsByListId(int listId) {
        return trelloRepo.getCardsByListId(listId);
    }
    
    @Override
    public Integer getBoardIdByListId(int listId) {
        return trelloRepo.getBoardIdByListId(listId);
    }

    public Integer getBoardIdByCardId(int cardId) {
        return trelloRepo.getBoardIdByCardId(cardId);
    }


    @Override
    public int getNextCardPosition(int listId) {
        return trelloRepo.getNextCardPosition(listId);
    }

    @Override
    public boolean assignMemberToCard(int cardId, int userId) {
        return trelloRepo.assignMemberToCard(cardId, userId);
    }

    @Override
    public List<UserBean> getCardMembers(int cardId) {
        return trelloRepo.getCardMembers(cardId);
    }
    
    @Override
    public boolean removeMemberFromCard(int cardId, int userId) {
        return trelloRepo.removeMemberFromCard(cardId, userId);
    }

    @Override
    public void logCardActivity(int cardId, int userId, String action) {
        trelloRepo.logCardActivity(cardId, userId, action);
    }
    
    @Override
    public void logBoardActivity(int boardId, int userId, String action) {
        trelloRepo.logBoardActivity(boardId, userId, action);
    }

    @Override
    public List<CardActivityBean> getCardActivities(int cardId) {
        return trelloRepo.getCardActivities(cardId);
    }
    
    //====================== due date =========================
    
    @Override
    
    public boolean updateDueDate(int cardId,java.sql.Date dueDate) {
        return trelloRepo.updateDueDate(cardId, dueDate);
    }



    // ===================== Attachments =====================
    @Override
    public boolean uploadAttachment(int cardId, int userId, MultipartFile file) {
        return trelloRepo.uploadAttachment(cardId, userId, file);
    }



    @Override
    public List<AttachmentBean> getAttachmentsByCardId(int cardId) {
        return trelloRepo.getAttachmentsByCardId(cardId);
    }

    @Override
    public AttachmentBean getAttachmentById(int id) {
        return trelloRepo.getAttachmentById(id);
    }
    
    @Override
    public List<CardMemberBean> getCardMembersWithDetails(int cardId) {
        return trelloRepo.getCardMembersWithDetails(cardId);
    }
    // ===================== Comments =====================
    @Override
    public boolean addCardComment(int cardId, int userId, String content) {
        return trelloRepo.addCardComment(cardId, userId, content);
    }


    @Override
    public List<CommentBean> getCommentsByCardId(int cardId) {
        return trelloRepo.getCommentsByCardId(cardId);
    }

    
    // ===================== Checklists =====================
    @Override
    public boolean addChecklist(int cardId, String title) {
        return trelloRepo.addChecklist(cardId, title);
    }

    @Override
    public List<ChecklistBean> getChecklistsByCardId(int cardId) {
        return trelloRepo.getChecklistsByCardId(cardId);
    }
    
    @Override
    public String getChecklistTitleById(int checklistId) {
        return trelloRepo.getChecklistTitleById(checklistId);
    }
    

    @Override
    public boolean addChecklistItem(int checklistId, String text) {
        boolean result = trelloRepo.addChecklistItem(checklistId, text);
        
        // Force refresh by querying the database again
        if (result) {
            // This forces a fresh query to the database
            trelloRepo.getChecklistItemsByChecklistId(checklistId);
        }
        
        return result;
    }
    @Override
    public List<ChecklistItemBean> getChecklistItemsByChecklistId(int checklistId) {
        return trelloRepo.getChecklistItemsByChecklistId(checklistId);
    }

    @Override
    public void toggleChecklistItem(int itemId) {
        ChecklistItemBean item = trelloRepo.getChecklistItemById(itemId);
        if (item != null) {
            item.setCompleted(!item.isCompleted());
            trelloRepo.updateChecklistItem(item);
        }
    }

    @Override
    public Integer getCardIdByChecklistId(int checklistId) {
        return trelloRepo.getCardIdByChecklistId(checklistId);
    }

   
    @Override
    public boolean updateChecklist(int checklistId, String title) {
        return trelloRepo.updateChecklist(checklistId, title);
    }

    @Override
    public boolean deleteChecklist(int checklistId) {
        return trelloRepo.deleteChecklist(checklistId);
    }


@Override
public boolean updateChecklistItem(int itemId, String text) {
    return trelloRepo.updateChecklistItem(itemId, text);
}



@Override
public boolean deleteChecklistItem(int itemId) {
    return trelloRepo.deleteChecklistItem(itemId);
}

	@Override
	public boolean updateCardDueDate(int cardId, String dueDate) {
		return false;
	}

    // ===================== Card Updates =====================
		
	@Override
	public UserBean getUserById(int userId) {
	    return trelloRepo.getUserById(userId);
	}

	 @Override
	    public boolean toggleCardCompletion(int cardId, int userId) {
	        try {
	            CardBean card = getCardById(cardId);
	            if (card == null) return false;
	            
	            boolean newCompletedState = !card.getCompleted();
	            return updateCardCompletion(cardId, newCompletedState, userId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	 @Override
	 public boolean updateCardCompletion(int cardId, boolean completed, int userId) {
	     try {
	         CardBean card = getCardById(cardId);
	         if (card == null) return false;

	         card.setCompleted(completed);
	         card.setCompletedAt(completed ? new Timestamp(System.currentTimeMillis()) : null);

	         trelloRepo.saveCard(card);

	         // ✅ Get extra info for logging
	         String cardTitle = card.getTitle();
	         String listTitle = (card.getList() != null) ? card.getList().getTitle() : "Unknown List";

	         String action = completed
	                 ? "completed card '" + cardTitle + "' in " + listTitle
	                 : "reopened card '" + cardTitle + "' in " + listTitle;

	         // ✅ Log at board-level
	         logBoardActivity(card.getList().getBoard().getId(), userId, action);

	         return true;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return false;
	     }
	 }

	    //drag and drop
	    
	    @Override
	    public void updateCardOrder(int listId, List<Integer> cardIds) {
	        for (int i = 0; i < cardIds.size(); i++) {
	            trelloRepo.updateCardPosition(cardIds.get(i), i);
	        }
	    }
	    
	    //see assignment
	    
	    @Override
	    public List<CardBean> getCardsAssignedToUser(int userId) {
	        return trelloRepo.getCardsAssignedToUser(userId);
	    }


}

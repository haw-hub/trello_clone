package com.spring.service;

import com.spring.enums.AddMemberResult;
import com.spring.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrelloService {

    // User
    UserBean login(String username, String password);
    UserBean getUserByEmail(String email);
    void registerUser(UserBean user);


    // Workspaces
    boolean createWorkspaceWithCreator(WorkspaceBean workspace);
    List<WorkspaceBean> getUserWorkspaces(int userId);
    List<Object[]> getWorkspacesWithRolesByUserId(int userId);
    WorkspaceBean getWorkspaceById(int workspaceId);
    WorkspaceMemberBean getWorkspaceMembership(int workspaceId, int userId);
    List<UserBean> getWorkspaceMembers(int workspaceId);
    boolean addWorkspaceMember(int workspaceId, int userId, String role);
    boolean removeWorkspaceMember(int workspaceId, int userId);
    boolean updateWorkspaceMemberRole(int workspaceId, int userId, String role);
    List<WorkspaceMemberBean> getWorkspaceMembersWithRole(int workspaceId);
    boolean updateWorkspace(int workspaceId, String name, String description);
    boolean deleteWorkspace(int workspaceId);


    // Boards
    boolean createBoard(BoardBean board, int creatorUserId);
    BoardBean getBoardById(int boardId);
    List<BoardBean> getBoardsByWorkspaceId(int workspaceId);
    boolean removeMemberFromBoard(int boardId, int userId);
    List<BoardMemberBean> getBoardMembersWithRole(int boardId);
    void updateBoardMemberRole(int boardId, int userId, String newRole);
    CardBean getCardById(int cardId);
    AddMemberResult addMemberToBoard(int boardId, int userId, String role);
    boolean updateBoardBackground(int boardId, String background);
    void removeBoard(int boardId);
    boolean updateBoard(int boardId, String title, String background);



    // Lists
    boolean addList(ListBean list, int boardId);
    boolean deleteList(int listId);
    List<ListBean> getListsByBoardId(int boardId);
    int getNextListPosition(int boardId);
    boolean updateListTitle(int listId, String newTitle);


    // Cards
    boolean addCard(CardBean card, int listId);
    boolean updateCard(int cardId, String title, String description);
    List<CardBean> getCardsByListId(int listId);
    int getNextCardPosition(int listId);
    boolean assignMemberToCard(int cardId, int userId);
    List<UserBean> getCardMembers(int cardId);
    Integer getBoardIdByListId(int listId);
    ListBean getListById(int listId);

    // --- Cards / Members ---
    Integer getBoardIdByCardId(int cardId);
    boolean removeMemberFromCard(int cardId, int userId);
    List<CardMemberBean> getCardMembersWithDetails(int cardId);
    void logCardActivity(int cardId, int userId, String action);
    void logBoardActivity(int boardId, int userId, String action);

    List<CardActivityBean> getCardActivities(int cardId);
    List<CardActivityBean> getBoardActivities(int boardId);
    boolean toggleCardCompletion(int cardId, int userId);
    boolean updateCardCompletion(int cardId, boolean completed, int userId);
    

    
    //due date 
    boolean updateDueDate(int cardId, java.sql.Date dueDate);



    // Attachments
    boolean uploadAttachment(int cardId, int userId, MultipartFile file);
    List<AttachmentBean> getAttachmentsByCardId(int cardId);
    AttachmentBean getAttachmentById(int id);

    // Comments
    boolean addCardComment(int cardId, int userId, String content);
    List<CommentBean> getCommentsByCardId(int cardId);

    // Checklists
    boolean addChecklist(int cardId, String title);
    List<ChecklistBean> getChecklistsByCardId(int cardId);
    boolean addChecklistItem(int checklistId, String text);
    List<ChecklistItemBean> getChecklistItemsByChecklistId(int checklistId);
    void toggleChecklistItem(int itemId);
    Integer getCardIdByChecklistId(int checklistId);
    boolean updateChecklist(int checklistId, String title);
    boolean deleteChecklist(int checklistId);
    boolean updateChecklistItem(int itemId, String text);
    boolean deleteChecklistItem(int itemId);
    String getChecklistTitleById(int checklistId);

    
    // Card updates
    boolean updateCardDueDate(int cardId, String dueDate);
    UserBean getUserById(int userId);
    
    //drag and drop
    void updateCardOrder(int listId, List<Integer> cardIds);
    
    //see assignment
    List<CardBean> getCardsAssignedToUser(int userId);


}

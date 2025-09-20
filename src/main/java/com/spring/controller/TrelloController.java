package com.spring.controller;

import com.spring.enums.AddMemberResult;
import com.spring.model.*;
import com.spring.service.TrelloService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller

public class TrelloController {

    @Autowired
    private TrelloService trelloService;
    
    @GetMapping("/")
    public String loginPage() {
        return "Login"; // Login.jsp
    }

    @PostMapping("/Login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        UserBean user = trelloService.login(username, password);
        if (user != null) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/dashboard"; // redirect to dashboard
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "Login";
        }
    }

    // ======= REGISTER =======
    @GetMapping("/Register")
    public String registerPage() {
        return "Register"; // Register.jsp
    }

    @PostMapping("/Register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        // Check if user exists
        if (trelloService.getUserByEmail(email) != null) {
            model.addAttribute("error", "Email already registered");
            return "Register";
        }

        // Save user
        UserBean user = new UserBean();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        trelloService.registerUser(user);

        model.addAttribute("message", "User registered successfully!");
        return "redirect:/"; // redirect to login
    }

    // ======= LOGOUT =======
    @GetMapping("/Logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ===================== Dashboard & Workspaces =====================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        // 🔹 Workspaces & Roles
        List<Object[]> workspaceData = trelloService.getWorkspacesWithRolesByUserId(user.getId());

        List<WorkspaceBean> adminWorkspaces = new ArrayList<>();
        List<WorkspaceBean> memberWorkspaces = new ArrayList<>();
        List<WorkspaceBean> allWorkspaces = new ArrayList<>();

        for (Object[] data : workspaceData) {
            WorkspaceBean workspace = (WorkspaceBean) data[0];
            String role = (String) data[1];

            allWorkspaces.add(workspace);

            if ("OWNER".equals(role) || "ADMIN".equals(role) ||
                (role == null && workspace.getCreated_by().equals(user.getId()))) {
                adminWorkspaces.add(workspace);
            } else {
                memberWorkspaces.add(workspace);
            }
        }

        // 🔹 My Assignments (cards assigned to logged-in user)
        List<CardBean> myAssignments = trelloService.getCardsAssignedToUser(user.getId());

        // 🔹 Add to model
        model.addAttribute("workspaces", allWorkspaces);
        model.addAttribute("adminWorkspaces", adminWorkspaces);
        model.addAttribute("memberWorkspaces", memberWorkspaces);
        model.addAttribute("loggedInUser", user);
        model.addAttribute("myAssignments", myAssignments);

        return "Dashboard"; // Dashboard.jsp
    }


    @GetMapping("workspace/{id}")
    public String viewWorkspace(@PathVariable("id") int workspaceId, HttpSession session, Model model) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        WorkspaceMemberBean membership = trelloService.getWorkspaceMembership(workspaceId, user.getId());
        if (membership == null) {
            return "redirect:/dashboard";
        }

        WorkspaceBean workspace = trelloService.getWorkspaceById(workspaceId);
        workspace.setCurrentUserRole(membership.getRole());

        List<BoardBean> boards = trelloService.getBoardsByWorkspaceId(workspaceId);
        List<WorkspaceMemberBean> members = trelloService.getWorkspaceMembersWithRole(workspaceId);
        
        // ADD THIS: Load workspaces for sidebar
        List<WorkspaceBean> allWorkspaces = trelloService.getUserWorkspaces(user.getId());
        
        // Separate workspaces by role for sidebar
        List<WorkspaceBean> adminWorkspaces = new ArrayList<>();
        List<WorkspaceBean> memberWorkspaces = new ArrayList<>();
        
        for (WorkspaceBean ws : allWorkspaces) {
            WorkspaceMemberBean wsMembership = trelloService.getWorkspaceMembership(ws.getId(), user.getId());
            if (wsMembership != null) {
                if ("OWNER".equals(wsMembership.getRole()) || "ADMIN".equals(wsMembership.getRole())) {
                    adminWorkspaces.add(ws);
                } else {
                    memberWorkspaces.add(ws);
                }
            } else if (ws.getCreated_by() != null && ws.getCreated_by().equals(user.getId())) {
                adminWorkspaces.add(ws);
            }
        }

        model.addAttribute("workspace", workspace);
        model.addAttribute("boards", boards);
        model.addAttribute("members", members);
        model.addAttribute("loggedInUser", user);
        
        // ADD THESE for sidebar
        model.addAttribute("adminWorkspaces", adminWorkspaces);
        model.addAttribute("memberWorkspaces", memberWorkspaces);
        model.addAttribute("selectedWorkspaceId", workspaceId); // This highlights the current workspace in sidebar

        return "WorkspaceView";
    }

    @PostMapping("/workspace/create")
    public String createWorkspace(@ModelAttribute WorkspaceBean workspace, HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/"; // redirect if not logged in

        workspace.setCreated_by(user.getId()); // set creator
        trelloService.createWorkspaceWithCreator(workspace);

        return "redirect:/dashboard"; // after creation
    }
    
    @PostMapping("/workspace/update")
    public String updateWorkspace(
            @RequestParam("workspaceId") int workspaceId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {

        boolean updated = trelloService.updateWorkspace(workspaceId, name, description);

        if (updated) {
            redirectAttributes.addFlashAttribute("wsmSuccess", "Workspace updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("wsmError", "Workspace not found or update failed.");
        }

        return "redirect:/dashboard"; // after creation
    }

    @PostMapping("/workspace/delete")
    public String deleteWorkspace(@RequestParam("workspaceId") int workspaceId,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        try {
            boolean ok = trelloService.deleteWorkspace(workspaceId);

            if (ok) {
                ra.addFlashAttribute("success", "Workspace deleted successfully.");
            } else {
                ra.addFlashAttribute("error", "Could not delete workspace.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Unexpected error while deleting workspace.");
        }

        return "redirect:/dashboard"; // After delete, go back to dashboard
    }


    
    @PostMapping("/workspace/add-member")
    public String addWorkspaceMember(
            @RequestParam("workspaceId") int workspaceId,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            HttpSession session,
            RedirectAttributes ra) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/"; // redirect if not logged in
        }

        // 1️⃣ Find user by email
        UserBean user = trelloService.getUserByEmail(email);
        if (user == null) {
            ra.addFlashAttribute("wsmError", "No user found with email: " + email);
            return "redirect:/workspace/" + workspaceId;
        }

        // 2️⃣ Add user to workspace
        boolean added = trelloService.addWorkspaceMember(workspaceId, user.getId(), role);
        if (!added) {
            ra.addFlashAttribute("wsmError", "User is already a member of this workspace.");
            return "redirect:/workspace/" + workspaceId;
        }

        ra.addFlashAttribute("wsmSuccess", "User added successfully!");
        return "redirect:/workspace/" + workspaceId; // redirect to workspace details
    }


    
    @PostMapping("/workspace/remove-member")
    public String removeWorkspaceMember(
            @RequestParam("workspaceId") int workspaceId,
            @RequestParam("userId") int userId,
            HttpSession session) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/"; // redirect if not logged in

        // Remove member
        trelloService.removeWorkspaceMember(workspaceId, userId);

        return "redirect:/workspace/" + workspaceId; // redirect to workspace details
    }

    @PostMapping("/workspace/change-role")
    public String changeMemberRole(
            @RequestParam("workspaceId") int workspaceId,
            @RequestParam("userId") int userId,
            @RequestParam("newRole") String newRole,
            HttpSession session) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/"; // redirect if not logged in

        // Validate role
        String roleToSet = "ADMIN".equalsIgnoreCase(newRole) ? "ADMIN" : "MEMBER";

        // Update role via service
        trelloService.updateWorkspaceMemberRole(workspaceId, userId, roleToSet);

        return "redirect:/workspace/" + workspaceId; // redirect back to workspace page
    }

    
    // ===================== Boards =====================
    
    @PostMapping("/board/create")
    public String createBoard(@RequestParam String title,
                              @RequestParam int workspaceId,
                              @RequestParam(value = "background", required = false) String background,
                              HttpSession session,
                              RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        try {
            WorkspaceBean workspace = trelloService.getWorkspaceById(workspaceId);
            if (workspace == null) {
                ra.addFlashAttribute("error", "Workspace not found.");
                return "redirect:/dashboard";
            }

            BoardBean board = new BoardBean();
            board.setTitle(title);
            board.setBackground(background != null ? background : "#2c2c3e");
            board.setWorkspace(workspace);         // link entity
            board.setWorkspaces_id(workspaceId);   // ✅ also set raw FK

            trelloService.createBoard(board, user.getId());

            ra.addFlashAttribute("success", "Board created successfully!");
            return "redirect:/workspace/" + workspaceId;
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to create board.");
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/board/remove")
    public String removeBoard(@RequestParam(required = false) Integer boardId,
                              @RequestParam(required = false) Integer workspaceId,
                              HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        if (boardId == null || workspaceId == null) {
            // handle gracefully if values are missing
            return "redirect:/dashboard";
        }

        trelloService.removeBoard(boardId);

        return "redirect:/workspace/" + workspaceId;
    }


    @PostMapping("/board/edit")
    public String editBoard(@RequestParam(required = false) Integer boardId,
                            @RequestParam(required = false) Integer workspaceId,
                            @RequestParam String title,
                            @RequestParam(required = false) String background,
                            HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        if (boardId == null || workspaceId == null) {
            return "redirect:/dashboard"; // fallback
        }

        if (background == null || background.trim().isEmpty()) {
            BoardBean existing = trelloService.getBoardById(boardId);
            if (existing != null) {
                background = existing.getBackground();
            }
        }

        trelloService.updateBoard(boardId, title, background);
        return "redirect:/workspace/" + workspaceId;
    }

    
    @GetMapping("/board/{id}")
    public String viewBoard(@PathVariable("id") int boardId, HttpSession session, Model model) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        try {
            // 🔹 Load board
            BoardBean board = trelloService.getBoardById(boardId);
            if (board == null) {
                return "redirect:/dashboard";
            }

            int workspaceId = board.getWorkspaces_id();

            // 🔹 Check membership
            WorkspaceMemberBean membership = trelloService.getWorkspaceMembership(workspaceId, user.getId());
            if (membership == null) {
                return "redirect:/dashboard";
            }

            WorkspaceBean workspace = trelloService.getWorkspaceById(workspaceId);
            workspace.setCurrentUserRole(membership.getRole());

            // 🔹 Load lists (with board set!)
            List<ListBean> lists = trelloService.getListsByBoardId(boardId);
            for (ListBean list : lists) {
                list.setBoard(board); // ✅ fix the missing relationship

                // Load cards
                List<CardBean> cards = trelloService.getCardsByListId(list.getId());
                for (CardBean card : cards) {
                    card.setList(list); // ✅ fix the missing relationship

                    // Load card members
                    List<UserBean> members = trelloService.getCardMembers(card.getId());
                    card.setMembers(members);

                    // Load attachments
                    card.setAttachments(trelloService.getAttachmentsByCardId(card.getId()));

                    // Load comments
                    card.setComments(trelloService.getCommentsByCardId(card.getId()));

                    // Load checklists with items
                    List<ChecklistBean> checklists = trelloService.getChecklistsByCardId(card.getId());
                    for (ChecklistBean checklist : checklists) {
                        List<ChecklistItemBean> items = trelloService.getChecklistItemsByChecklistId(checklist.getId());
                        checklist.setItems(items);
                    }
                    card.setChecklists(checklists);
                }

                list.setCards(cards);
            }

            // 🔹 Workspace + board members
            List<UserBean> workspaceMembers = trelloService.getWorkspaceMembers(workspaceId);
            List<BoardMemberBean> boardMembers = trelloService.getBoardMembersWithRole(boardId);

            // 🔹 Sidebar workspaces
            List<WorkspaceBean> allWorkspaces = trelloService.getUserWorkspaces(user.getId());
            List<WorkspaceBean> adminWorkspaces = new ArrayList<>();
            List<WorkspaceBean> memberWorkspaces = new ArrayList<>();

            for (WorkspaceBean ws : allWorkspaces) {
                WorkspaceMemberBean wsMembership = trelloService.getWorkspaceMembership(ws.getId(), user.getId());
                if (wsMembership != null) {
                    if ("OWNER".equals(wsMembership.getRole()) || "ADMIN".equals(wsMembership.getRole())) {
                        adminWorkspaces.add(ws);
                    } else {
                        memberWorkspaces.add(ws);
                    }
                } else if (ws.getCreated_by() != null && ws.getCreated_by().equals(user.getId())) {
                    adminWorkspaces.add(ws);
                }
            }

            // 🔹 Activities
            List<CardActivityBean> boardActivities = trelloService.getBoardActivities(boardId);

            // 🔹 Push to model
            model.addAttribute("board", board);
            model.addAttribute("lists", lists);
            model.addAttribute("workspaceMembers", workspaceMembers);
            model.addAttribute("boardMembers", boardMembers);
            model.addAttribute("workspaceId", workspaceId);
            model.addAttribute("loggedInUser", user);
            model.addAttribute("workspace", workspace);
            model.addAttribute("adminWorkspaces", adminWorkspaces);
            model.addAttribute("memberWorkspaces", memberWorkspaces);
            model.addAttribute("selectedWorkspaceId", workspaceId);
            model.addAttribute("boardActivities", boardActivities);
            model.addAttribute("now", new java.util.Date());

            return "BoardDetail";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard";
        }
    }


 // Update background color
    @PostMapping("/{boardId}/updateBackground")
    public String updateBackground(@PathVariable("boardId") int boardId,
                                   @RequestParam("background") String background,
                                   RedirectAttributes ra) {
        if (trelloService.updateBoardBackground(boardId, background)) {
            ra.addFlashAttribute("success", "Board background updated!");
        } else {
            ra.addFlashAttribute("error", "Failed to update background.");
        }
        return "redirect:/board/" + boardId;
    }
    
    // ===================== Board Members =====================

    @PostMapping("/board/add-member")
    public String addBoardMember(
            @RequestParam int boardId,
            @RequestParam int userId,
            @RequestParam String role,
            RedirectAttributes ra) {

        AddMemberResult result = trelloService.addMemberToBoard(boardId, userId, role);

        switch (result) {
            case SUCCESS -> ra.addFlashAttribute("boardSuccess", "User added to board successfully!");
            case DUPLICATE -> ra.addFlashAttribute("boardError", "This user is already a member of the board.");
            case NOT_FOUND -> ra.addFlashAttribute("boardError", "Board or user not found.");
            case ERROR -> ra.addFlashAttribute("boardError", "Something went wrong while adding the user.");
        }

        return "redirect:/board/" + boardId;
    }



    @PostMapping("board/remove-member")
    public String removeBoardMember(@RequestParam int boardId, @RequestParam int userId) {
        trelloService.removeMemberFromBoard(boardId, userId);
        return "redirect:/board/" + boardId;
        
        
        
    }
    
    @PostMapping("/board/change-role")
    public String changeBoardMemberRole(
            @RequestParam("boardId") int boardId,
            @RequestParam("userId") int userId,
            @RequestParam("newRole") String newRole,
            HttpSession session) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/"; // not logged in → redirect
        }

        // validate role
        String roleToSet = "ADMIN".equalsIgnoreCase(newRole) ? "ADMIN" : "MEMBER";

        // update via service
        trelloService.updateBoardMemberRole(boardId, userId, roleToSet);

        return "redirect:/board/" + boardId; // reload board detail page
    }

    //======================Lists=============================
    
    @PostMapping("/list/add")
    public String addList(
            @RequestParam("boardId") int boardId,
            @RequestParam("title") String title,
            HttpSession session,
            RedirectAttributes ra) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/";

        try {
            // 🔹 Fetch board
            BoardBean board = trelloService.getBoardById(boardId);
            if (board == null) {
                ra.addFlashAttribute("error", "Board not found.");
                return "redirect:/dashboard";
            }

            // 🔹 Build ListBean
            ListBean list = new ListBean();
            list.setTitle(title);
            list.setPosition(trelloService.getNextListPosition(boardId));

            // ✅ Set relationship
            list.setBoard(board);
            list.setBoards_id(board.getId());  // if you also keep boardId column separately

            // 🔹 Save
            trelloService.addList(list, boardId);

            // 🔹 Log activity
            trelloService.logBoardActivity(
                    boardId,
                    loggedInUser.getId(),
                    "Created list: " + title
            );

            ra.addFlashAttribute("success", "List added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to add list.");
        }

        return "redirect:/board/" + boardId;
    }


    
    @PostMapping("/list/update")
    public String updateListTitle(
            @RequestParam("listId") int listId,
            @RequestParam("title") String title,
            HttpSession session,
            RedirectAttributes ra) {

        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        Integer boardId = trelloService.getBoardIdByListId(listId);

        try {
            boolean updated = trelloService.updateListTitle(listId, title);

            if (updated) {
                ra.addFlashAttribute("listSuccess", "List title updated successfully!");

                // ✅ Log board activity
                if (boardId != null) {
                    trelloService.logBoardActivity(
                            boardId,
                            user.getId(),
                            "Renamed list to: " + title
                    );
                }

            } else {
                ra.addFlashAttribute("listError", "Failed to update list title.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("listError", "Unexpected error updating list title.");
        }

        return (boardId != null) ? ("redirect:/board/" + boardId) : "redirect:/dashboard";
    }

    @PostMapping("/list/{listId}/delete")
    public String deleteList(@PathVariable("listId") int listId,
                             HttpSession session,
                             RedirectAttributes ra) {

        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        try {
            // fetch list with board
            ListBean list = trelloService.getListById(listId);
            if (list == null) {
                ra.addFlashAttribute("error", "List not found.");
                return "redirect:/dashboard";
            }

            int boardId = list.getBoard().getId();

            // delete
            boolean ok = trelloService.deleteList(listId);

            if (ok) {
                ra.addFlashAttribute("success", "List deleted successfully.");

                // log activity
                trelloService.logBoardActivity(
                    boardId,
                    user.getId(),
                    "Deleted list: " + list.getTitle()
                );
            } else {
                ra.addFlashAttribute("error", "Could not delete list.");
            }

            return "redirect:/board/" + boardId;
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Unexpected error deleting list.");
            return "redirect:/dashboard";
        }
    }


    // ===================== Cards =====================
    
 
    @PostMapping("/card/add")
    public String addCard(@RequestParam("listId") int listId,
                          @RequestParam("title") String title,
                          @RequestParam(value = "description", required = false) String description,
                          HttpSession session,
                          RedirectAttributes ra) {

        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }

        try {
            ListBean list = trelloService.getListById(listId);
            if (list == null) {
                ra.addFlashAttribute("error", "List not found.");
                return "redirect:/dashboard";
            }

            CardBean card = new CardBean();
            card.setTitle(title);
            card.setDescription(description);
            card.setList(list);  // ✅ now persists lists_id automatically
            card.setLists_id(listId); // ✅ makes sure lists_id is not null

            card.setPosition(trelloService.getNextCardPosition(listId));

            boolean ok = trelloService.addCard(card, listId);

            if (ok) {
                ra.addFlashAttribute("success", "Card created.");
                trelloService.logBoardActivity(
                	    list.getBoard().getId(),
                	    user.getId(),
                	    "Created card: " + title + " of " + list.getTitle()
                	);

            } else {
                ra.addFlashAttribute("error", "Could not add card.");
            }

            return "redirect:/board/" + list.getBoard().getId();

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Unexpected error creating card.");
            return "redirect:/dashboard";
        }
    }


    
    @PostMapping("/card/{cardId}/edit")
    public String editCard(@PathVariable("cardId") int cardId,
                           @RequestParam("title") String title,
                           @RequestParam(value = "description", required = false) String description,
                           HttpSession session,
                           RedirectAttributes ra) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        try {
            boolean updated = trelloService.updateCard(cardId, title, description);

            if (updated) {
                // ✅ Log history
                trelloService.logCardActivity(
                    cardId,
                    loggedInUser.getId(),
                    "Edited card: " + title
                );
                ra.addFlashAttribute("success", "Card updated successfully!");
            } else {
                ra.addFlashAttribute("error", "Could not update card.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Something went wrong while updating card.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? ("redirect:/board/" + boardId) : "redirect:/dashboard";
    }


    

    @PostMapping("/card/{cardId}/assign")
    public String assignMember(@PathVariable int cardId,
                               @RequestParam("userId") int userId,
                               HttpSession session,
                               RedirectAttributes ra) {

        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        boolean success = trelloService.assignMemberToCard(cardId, userId);

        if (success) {
            ra.addFlashAttribute("success", "Member assigned successfully!");

            // ✅ Get assigned user's name
            UserBean assignedUser = trelloService.getUserById(userId);
            String assignedName = (assignedUser != null) ? assignedUser.getUsername() : "Unknown User";

            // ✅ Get card + list info
            CardBean card = trelloService.getCardById(cardId);
            String cardName = (card != null) ? card.getTitle() : "Unknown Card";
            String listName = (card != null && card.getList() != null) ? card.getList().getTitle() : "Unknown List";

            // ✅ Log activity with context
            String activityText = String.format(
                "assigned member: %s to %s of %s",
                assignedName,
                cardName,
                listName
            );

            trelloService.logCardActivity(
                cardId,
                loggedInUser.getId(),
                activityText
            );

        } else {
            ra.addFlashAttribute("error", "Failed to assign member.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }



    @PostMapping("/card/{cardId}/remove")
    public String removeMember(@PathVariable("cardId") int cardId,
                               @RequestParam("userId") int userId,
                               HttpSession session,
                               RedirectAttributes ra) {
        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        boolean success = trelloService.removeMemberFromCard(cardId, userId);

        if (success) {
            ra.addFlashAttribute("success", "Member removed successfully!");

            // ✅ Get removed user's name
            UserBean removedUser = trelloService.getUserById(userId);
            String removedName = (removedUser != null) ? removedUser.getUsername() : "Unknown User";

            // ✅ Get card + list info
            CardBean card = trelloService.getCardById(cardId);
            String cardName = (card != null) ? card.getTitle() : "Unknown Card";
            String listName = (card != null && card.getList() != null) ? card.getList().getTitle() : "Unknown List";

            // ✅ Log activity with full context
            String activityText = String.format(
                "removed member: %s in %s of %s",
                removedName,
                cardName,
                listName
            );

            trelloService.logCardActivity(
                cardId,
                loggedInUser.getId(),
                activityText
            );

        } else {
            ra.addFlashAttribute("error", "Could not remove member.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? ("redirect:/board/" + boardId) : "redirect:/dashboard";
    }




    @PostMapping("/card/{cardId}/comment")
    public String addComment(@PathVariable("cardId") int cardId,
                             @RequestParam("content") String content,  // changed!
                             HttpSession session,
                             RedirectAttributes ra) {
        UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }

        if (trelloService.addCardComment(cardId, loggedInUser.getId(), content)) {
            ra.addFlashAttribute("success", "Comment added successfully!");
        } else {
            ra.addFlashAttribute("error", "Failed to add comment.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? ("redirect:/board/" + boardId) : "redirect:/dashboard";
    }


    @PostMapping("/card/{cardId}/uploadAttachment")
    public String uploadAttachment(@PathVariable int cardId,
                                   @RequestParam("file") MultipartFile file,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        try {
            UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                return "redirect:/";
            }

            if (trelloService.uploadAttachment(cardId, loggedInUser.getId(), file)) {
                // step 4: log activity
                trelloService.logCardActivity(
                    cardId,
                    loggedInUser.getId(),
                    "Added attachment: " + file.getOriginalFilename()
                );
                ra.addFlashAttribute("success", "Attachment uploaded successfully!");
            } else {
                ra.addFlashAttribute("error", "Failed to upload attachment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Failed to upload attachment.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }



    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable int id) {
        try {
            AttachmentBean attachment = trelloService.getAttachmentById(id);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(attachment.getFileType()));
            headers.setContentDispositionFormData("attachment", attachment.getFilename());
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(attachment.getFile());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/card/{cardId}/updateDueDate")
    public String updateDueDate(@PathVariable("cardId") int cardId,
                                @RequestParam("dueDate") String dueDateStr,
                                HttpSession session,
                                RedirectAttributes ra) {
        try {
            UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                return "redirect:/";
            }

            java.sql.Date dueDate = java.sql.Date.valueOf(dueDateStr); // Convert String → Date

            if (trelloService.updateDueDate(cardId, dueDate)) {

                // ✅ Get card + list info
                CardBean card = trelloService.getCardById(cardId);
                String cardName = (card != null) ? card.getTitle() : "Unknown Card";
                String listName = (card != null && card.getList() != null) ? card.getList().getTitle() : "Unknown List";

                // ✅ Log activity with full context
                String activityText = String.format(
                    "updated due date to %s in %s of %s",
                    dueDate.toString(),
                    cardName,
                    listName
                );

                trelloService.logCardActivity(
                    cardId,
                    loggedInUser.getId(),
                    activityText
                );

                ra.addFlashAttribute("success", "Due date updated successfully!");
            } else {
                ra.addFlashAttribute("error", "Failed to update due date.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Invalid date format.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }



    @PostMapping("/card/{cardId}/toggle-completion")
    public String toggleCardCompletion(@PathVariable("cardId") int cardId,
                                      HttpSession session,
                                      RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }
        
        try {
            boolean success = trelloService.toggleCardCompletion(cardId, user.getId());
            if (success) {
                ra.addFlashAttribute("success", "Card status updated!");
            } else {
                ra.addFlashAttribute("error", "Failed to update card status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error updating card status.");
        }
        
        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }
    
    @PostMapping("/card/{cardId}/mark-complete")
    public String markCardComplete(@PathVariable("cardId") int cardId,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }
        
        try {
            boolean success = trelloService.updateCardCompletion(cardId, true, user.getId());
            if (success) {
                ra.addFlashAttribute("success", "Card marked as complete!");
            } else {
                ra.addFlashAttribute("error", "Failed to mark card as complete.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error marking card as complete.");
        }
        
        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }
    
    @PostMapping("/card/{cardId}/mark-incomplete")
    public String markCardIncomplete(@PathVariable("cardId") int cardId,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }
        
        try {
            boolean success = trelloService.updateCardCompletion(cardId, false, user.getId());
            if (success) {
                ra.addFlashAttribute("success", "Card reopened!");
            } else {
                ra.addFlashAttribute("error", "Failed to reopen card.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error reopening card.");
        }
        
        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }
    

    // ===================== Checklists =====================

    @PostMapping("/checklist/add")
    public String addChecklist(@RequestParam("cards_id") int cardId,
                               @RequestParam("title") String title,
                               HttpSession session,
                               RedirectAttributes ra) {
        try {
            UserBean loggedInUser = (UserBean) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                return "redirect:/";
            }

            if (trelloService.addChecklist(cardId, title)) {
                // ✅ Get card + list info
                CardBean card = trelloService.getCardById(cardId);
                String cardName = (card != null) ? card.getTitle() : "Unknown Card";
                String listName = (card != null && card.getList() != null) ? card.getList().getTitle() : "Unknown List";

                // ✅ Log activity with full context
                String activityText = String.format(
                    "added checklist \"%s\" in %s of %s",
                    title,
                    cardName,
                    listName
                );

                trelloService.logCardActivity(
                    cardId,
                    loggedInUser.getId(),
                    activityText
                );

                ra.addFlashAttribute("success", "Checklist added successfully!");
            } else {
                ra.addFlashAttribute("error", "Failed to add checklist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error while adding checklist.");
        }

        Integer boardId = trelloService.getBoardIdByCardId(cardId);
        return (boardId != null) ? ("redirect:/board/" + boardId) : "redirect:/dashboard";
    }

    
 
    @PostMapping("/checklist/item/add")
    public String addChecklistItem(@RequestParam("checklists_id") int checklistId,
                                  @RequestParam("text") String text,
                                  RedirectAttributes ra,
                                  HttpSession session) {
        
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/";
        }
        
        if (trelloService.addChecklistItem(checklistId, text)) {
            ra.addFlashAttribute("success", "Checklist item added successfully!");
        } else {
            ra.addFlashAttribute("error", "Failed to add checklist item.");
        }
        
        // Redirect with force refresh parameter
        Integer cardId = trelloService.getCardIdByChecklistId(checklistId);
        if (cardId != null) {
            Integer boardId = trelloService.getBoardIdByCardId(cardId);
            return (boardId != null) ? ("redirect:/board/" + boardId + "?refresh=true") : "redirect:/dashboard";
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/checklist/item/toggle")
    public String toggleChecklistItem(@RequestParam("item_id") int itemId,
                                     @RequestParam("checklists_id") int checklistId,
                                     RedirectAttributes ra) {
        try {
            trelloService.toggleChecklistItem(itemId);
            ra.addFlashAttribute("success", "Checklist item updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update checklist item.");
        }
        
        // Redirect back to the board page
        Integer cardId = trelloService.getCardIdByChecklistId(checklistId);
        if (cardId != null) {
            Integer boardId = trelloService.getBoardIdByCardId(cardId);
            return (boardId != null) ? ("redirect:/board/" + boardId) : "redirect:/dashboard";
        }
        return "redirect:/dashboard";
    }
    
    @PostMapping("/checklist/update")
    public String updateChecklist(@RequestParam("checklists_id") int checklistId,
                                  @RequestParam("title") String title,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        Integer cardId = trelloService.getCardIdByChecklistId(checklistId);

        if (trelloService.updateChecklist(checklistId, title)) {
            // ✅ Get card + list + old checklist info
            CardBean card = trelloService.getCardById(cardId);
            String cardName = (card != null) ? card.getTitle() : "Unknown Card";
            String listName = (card != null && card.getList() != null) ? card.getList().getTitle() : "Unknown List";

            String activityText = String.format(
                "updated checklist title to \"%s\" in %s of %s",
                title,
                cardName,
                listName
            );

            trelloService.logCardActivity(cardId, user.getId(), activityText);
            ra.addFlashAttribute("success", "Checklist updated!");
        } else {
            ra.addFlashAttribute("error", "Failed to update checklist.");
        }

        Integer boardId = (cardId != null) ? trelloService.getBoardIdByCardId(cardId) : null;
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }

    @PostMapping("/checklist/delete")
    public String deleteChecklist(@RequestParam("checklists_id") int checklistId,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        Integer cardId = trelloService.getCardIdByChecklistId(checklistId);

        // ✅ Try to fetch checklist title before deletion
        String checklistTitle = trelloService.getChecklistTitleById(checklistId);

        if (trelloService.deleteChecklist(checklistId)) {
            CardBean card = trelloService.getCardById(cardId);
            String cardName = (card != null) ? card.getTitle() : "Unknown Card";
            String listName = (card != null && card.getList() != null) ? card.getList().getTitle() : "Unknown List";

            String activityText = String.format(
                "deleted checklist \"%s\" in %s of %s",
                (checklistTitle != null) ? checklistTitle : "Unnamed Checklist",
                cardName,
                listName
            );

            trelloService.logCardActivity(cardId, user.getId(), activityText);
            ra.addFlashAttribute("success", "Checklist deleted!");
        } else {
            ra.addFlashAttribute("error", "Failed to delete checklist.");
        }

        Integer boardId = (cardId != null) ? trelloService.getBoardIdByCardId(cardId) : null;
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }

    
    @PostMapping("/checklist/item/update")
    public String updateChecklistItem(@RequestParam("item_id") int itemId,
                                      @RequestParam("checklists_id") int checklistId,
                                      @RequestParam("text") String text,
                                      HttpSession session,
                                      RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        if (trelloService.updateChecklistItem(itemId, text)) {
            trelloService.logCardActivity(
                    trelloService.getCardIdByChecklistId(checklistId),
                    user.getId(),
                    "Updated checklist item: " + text
            );
            ra.addFlashAttribute("success", "Checklist item updated!");
        } else {
            ra.addFlashAttribute("error", "Failed to update checklist item.");
        }

        Integer cardId = trelloService.getCardIdByChecklistId(checklistId);
        Integer boardId = (cardId != null) ? trelloService.getBoardIdByCardId(cardId) : null;
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }

    @PostMapping("/checklist/item/delete")
    public String deleteChecklistItem(@RequestParam("item_id") int itemId,
                                      @RequestParam("checklists_id") int checklistId,
                                      HttpSession session,
                                      RedirectAttributes ra) {
        UserBean user = (UserBean) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        if (trelloService.deleteChecklistItem(itemId)) {
            trelloService.logCardActivity(
                    trelloService.getCardIdByChecklistId(checklistId),
                    user.getId(),
                    "Deleted a checklist item"
            );
            ra.addFlashAttribute("success", "Checklist item deleted!");
        } else {
            ra.addFlashAttribute("error", "Failed to delete checklist item.");
        }

        Integer cardId = trelloService.getCardIdByChecklistId(checklistId);
        Integer boardId = (cardId != null) ? trelloService.getBoardIdByCardId(cardId) : null;
        return (boardId != null) ? "redirect:/board/" + boardId : "redirect:/dashboard";
    }

    
    //==========drag and drop==============
    
    
    @PostMapping("/card/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderCards(@RequestBody Map<String, Object> payload) {
        try {
            Integer listId = Integer.parseInt(payload.get("listId").toString());
            List<Integer> cardIds = ((List<?>) payload.get("cardIds"))
                .stream().map(id -> Integer.parseInt(id.toString()))
                .toList();

            trelloService.updateCardOrder(listId, cardIds);
            return ResponseEntity.ok("Order updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating order");
        }
    }

}

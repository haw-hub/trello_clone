<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>${board.title} - Board Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        .back-btn {
            color: #00bcd4;
            border-color: #00bcd4;
        }
        .back-btn:hover {
            background-color: #00bcd4;
            color: white;
        }
        
        /* Custom scrollbar styling */
        .lists-container {
            display: flex;
            gap: 1rem;
            overflow-x: auto;
            padding-bottom: 1rem; /* Space for scrollbar */
            min-height: calc(100vh - 200px); /* Adjust based on your header height */
        }
        
        /* Scrollbar styling */
        .lists-container::-webkit-scrollbar {
            height: 10px;
        }
        
        .lists-container::-webkit-scrollbar-track {
            background: #343a40;
            border-radius: 10px;
        }
        
        .lists-container::-webkit-scrollbar-thumb {
            background: #00bcd4;
            border-radius: 10px;
        }
        
        .lists-container::-webkit-scrollbar-thumb:hover {
            background: #008fa1;
        }
        
        .list-column {
            min-width: 280px;
            background-color: #2c3034;
            border-radius: 0.375rem;
            padding: 1rem;
            height: fit-content;
        }
        
        .add-list-btn {
            min-width: 280px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            color: white;
            border: 2px dashed white;
            border-radius: 0.375rem;
        }
        
        .add-list-btn:hover {
            background-color: gray;
        }
        
        /* Trello-like Card Styles */
        .trello-card-link {
            text-decoration: none;
            color: inherit;
            display: block;
            margin-bottom: 8px;
        }

        .trello-card {
            background-color: #fff;
            border-radius: 3px;
            box-shadow: 0 1px 0 rgba(9,30,66,.25);
            padding: 8px 12px;
            cursor: pointer;
            max-width: 300px;
            min-height: 20px;
            position: relative;
            text-decoration: none;
            word-wrap: break-word;
            transition: all 0.1s ease;
            border-left: 4px solid #4bce97; /* Default green edge, you can make this dynamic */
        }

        .trello-card:hover {
            background-color: #f5f6f8;
            box-shadow: 0 1px 0 rgba(9,30,66,.25), 0 2px 4px rgba(9,30,66,.15);
        }

        .trello-card:active {
            background-color: #e4f0f6;
        }

        .card-content {
            font-size: 14px;
            line-height: 20px;
            font-weight: 400;
            color: #172b4d;
        }

        /* Optional: Add these if you want to include labels, members, etc. */
        .card-labels {
            margin-bottom: 6px;
        }

        .card-label {
            height: 8px;
            border-radius: 4px;
            display: inline-block;
            margin-right: 4px;
            width: 40px;
        }

        .card-members {
            margin-top: 8px;
            display: flex;
        }

        .member-avatar {
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background-color: #dfe1e6;
            color: #172b4d;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: bold;
            margin-right: 4px;
        }
        
        .card-members .badge {
            font-size: 0.75rem;
            padding: 4px 8px;
            border-radius: 999px;
        }
        
        /* Member avatars */
        .member-avatar {
            width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            font-weight: bold;
            font-size: 12px;
            color: white;
            box-shadow: 0 0 0 2px #ffffff;
            text-transform: uppercase;
        }

        /* Color classes for members */
        .member-color-1 { background-color: #4bce97; }
        .member-color-2 { background-color: #f5cd47; }
        .member-color-3 { background-color: #faa53d; }
        .member-color-4 { background-color: #f87462; }
        .member-color-5 { background-color: #9f8fef; }
        .member-color-6 { background-color: #579dff; }
        .member-color-7 { background-color: #60c6d2; }
        .member-color-8 { background-color: #94c748; }
        .member-color-9  { background-color: #e774bb; }  /* Pink */
		.member-color-10 { background-color: #6e5dc6; }  /* Indigo */

        /* Card members container */
        .card-members {
            display: flex;
            flex-wrap: wrap;
            gap: 4px;
            align-items: center;
        }
        
        .btn-remove {
            background: none;
            border: none;
            color: #ff6b6b;
            cursor: pointer;
            padding: 0;
            font-size: 14px;
        }
        
        .btn-remove:hover {
            color: #ff3838;
        }
        .icon-circle {
			  display: flex;               /* center icon */
			  align-items: center;
			  justify-content: center;
			  width: 32px;                 /* fixed size */
			  height: 32px;                /* same as width → circle */
			  border-radius: 50%;          /* makes it round */
			  flex-shrink: 0;              /* prevents shrinking */
			  font-size: 16px;             /* icon size */
			}
		textarea::placeholder {
			    color: white !important;   /* white */
			    
			}
        /* Base for all icons */
			.toggle-btn {
			    border: none !important;
			    background: transparent !important;
			    padding: 4px 6px;
			    transition: all 0.2s ease-in-out;
			}
			
			.toggle-btn:hover {
			    background: rgba(255, 255, 255, 0.15); /* lighter hover effect for dark bg */
			    border-radius: 6px;
			}
			
			/* Active (expanded) state */
			.toggle-btn[aria-expanded="true"] {
			    border: 1px solid currentColor !important;
			    border-radius: 6px;
			    background: rgba(255, 255, 255, 0.1);
			}
			
			/* Special style for collapse toggle → white icon */
			.toggle-btn-white {
			    color: #fff !important;
			}

        
    </style>
</head>

<body class="bg-dark text-light">
<%@ include file="navbar.jsp" %>

<div style="background-color: ${board.background}; min-height: 100vh; width: 100%; margin: 0; padding: 0;">

    <!-- Sticky header with dark background -->
    <div class="sticky-top bg-dark pt-1 pb-3" style="position: sticky; top:55.5px; z-index:900;">
        <div class="d-flex justify-content-between mt-3 align-items-center px-3">
            <h3 class="text-white mb-0">${board.title}</h3>
            
            <div class="d-flex align-items-center gap-2">
                <div class="dropdown">
                    <button class="btn btn-sm btn-outline-light dropdown-toggle" 
                            type="button" 
                            id="memberDropdown" 
                            data-bs-toggle="dropdown" 
                            aria-expanded="false">
                        <i class="fas fa-users me-1"></i> 
                        Board Members (${boardMembers.size()})
                    </button>

                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end" 
                        aria-labelledby="memberDropdown" 
                        style="min-width: 320px;">
                        <c:choose>
                            <c:when test="${not empty boardMembers}">
                                <c:forEach var="member" items="${boardMembers}">
                                    <li class="dropdown-item px-3 py-2">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <!-- Left: User info -->
                                            <div class="d-flex align-items-center">
                                                <div class="me-3">
                                                    <div class="fw-semibold text-truncate text-white" style="max-width: 150px;">
                                                        ${member.user.username}
                                                    </div>
                                                    <small class="text-secondary text-truncate" style="max-width: 150px;">
                                                        ${member.user.email}
                                                    </small>
                                                </div>
                                            </div>

                                            <!-- Right: Role + Remove -->
                                            <c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
                                                <div class="d-flex align-items-center">
                                                    <c:choose>
                                                        <c:when test="${member.role eq 'OWNER'}">
                                                            <span class="badge bg-info text-dark me-2">
                                                                <i class="fas fa-crown me-1"></i> Owner
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <!-- Role switch -->
                                                            <form method="post" 
                                                                  action="${pageContext.request.contextPath}/board/change-role"
                                                                  class="me-2 d-flex align-items-center"
                                                                  onsubmit="return confirm('Are you sure you want to change ${member.user.username} role?');">

                                                                <input type="hidden" name="boardId" value="${board.id}" />
                                                                <input type="hidden" name="userId" value="${member.user.id}" />
                                                                <input type="hidden" id="newRole-${member.user.id}" name="newRole" value="${member.role}" />

                                                                <div class="form-check form-switch">
                                                                    <input class="form-check-input"
                                                                           type="checkbox"
                                                                           id="roleSwitch-${member.user.id}"
                                                                           ${member.role eq 'ADMIN' ? 'checked' : ''}
                                                                           onchange="
                                                                               document.getElementById('newRole-${member.user.id}').value = this.checked ? 'ADMIN' : 'MEMBER';
                                                                               this.form.submit();
                                                                           " />
                                                                    <label class="form-check-label text-light ms-2" for="roleSwitch-${member.user.id}">
                                                                        ${member.role eq 'ADMIN' ? 'Admin' : 'Member'}
                                                                    </label>
                                                                </div>
                                                            </form>

                                                            <!-- Remove button -->
                                                            <form method="post" 
                                                                  action="${pageContext.request.contextPath}/board/remove-member"
                                                                  onsubmit="return confirm('Remove ${member.user.username} from this board?');">
                                                                <input type="hidden" name="boardId" value="${board.id}" />
                                                                <input type="hidden" name="userId" value="${member.user.id}" />
                                                                <button type="submit" class="btn-remove ms-2" title="Remove Member">
                                                                    <i class="fas fa-times"></i>
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </c:if>
                                        </div>
                                    </li>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <li><span class="dropdown-item text-light text-center">No members in this workspace yet.</span></li>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
                            <li class="px-3 py-2 border-top border-secondary">
                                <button class="btn w-100 text-white" 
                                        style="background-color: darkcyan;" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#addMemberModal-${board.id}">
                                    <i class="fas fa-user-plus me-1"></i> Invite Member
                                </button>
                            </li>
                        </c:if>
                    </ul>
                </div>
                
                <a href="${pageContext.request.contextPath}/workspace/${board.workspaces_id}" 
                   class="btn btn-sm btn-outline-light">
                    ← Back to Boards
                </a>
                <a href="${pageContext.request.contextPath}/dashboard" 
				   class="btn btn-sm btn-outline-light">
				    ← Back to Dashboard
				</a>
                
                
                <button class="btn btn-sm btn-outline-light" data-bs-toggle="offcanvas" data-bs-target="#activitySidebar">
				  <i class="bi bi-clock-history"></i> Activity
				</button>
                
            </div>
        </div>
    </div>

    <div class="lists-container mt-3 ms-3">
		<c:forEach var="list" items="${lists}">
        <div class="list-column">
            <!-- List header with 3-dot dropdown -->
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="mb-0">${list.title}</h5>
			<c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
			
                <div class="dropdown">
                    <button class="btn btn-sm btn-outline-light" 
                            type="button" 
                            id="dropdownMenu${list.id}" 
                            data-bs-toggle="dropdown" 
                            aria-expanded="false">
                        <i class="bi bi-three-dots-vertical"></i>
                    </button>
                    
                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end" aria-labelledby="dropdownMenu${list.id}">
                        <li>
                            <a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#editListModal${list.id}">
                                <i class="bi bi-pencil me-2"></i>Edit List
                            </a>
                        </li>
                            <li>
                                <form action="${pageContext.request.contextPath}/list/${list.id}/delete" method="post" onsubmit="return confirm('Are you sure you want to delete this list?');">
								    <input type="hidden" name="listId" value="${list.id}" />
								    <button type="submit" class="dropdown-item text-danger">
								        <i class="bi bi-trash me-2"></i>Delete List
								    </button>
								</form>

                                
                            </li>
                        
                    </ul>
                    
                </div>
                </c:if>
            </div>

            <!-- Cards -->
            
            <c:forEach var="card" items="${list.cards}">
    <!-- Card Wrapper (click anywhere to open detail modal) -->
    <div class="trello-card p-3 bg-white rounded-3 shadow-sm border border-light mb-3 ${card.completed ? 'completed opacity-75' : ''}"
         onclick="handleCardClick(${card.id}, ${card.completed})" 
         style="cursor:${card.completed ? 'default' : 'pointer'};">

        <!-- Completion badge -->
        <c:if test="${card.completed}">
            <div class="completion-badge bg-success text-white px-2 py-1 rounded mb-2 d-inline-block">
                <i class="bi bi-check-circle-fill me-1"></i>Completed
            </div>
            <div class="completion-info small text-muted">
                <fmt:formatDate value="${card.completedAt}" pattern="MMM d, yyyy 'at' h:mm a"/>
            </div>
        </c:if>
        
        <div class="d-flex justify-content-between align-items-center mb-2">
            <!-- Card Title -->
            <div class="fw-semibold text-dark ${card.completed ? 'text-decoration-line-through' : ''}">
                ${card.title}
            </div>

            <!-- Edit Card Button (disabled when completed) -->
            <c:if test="${not card.completed}">
                <button type="button" class="btn btn-sm btn-light border-0 shadow-sm rounded-circle"
                        onclick="openEditModal(${card.id}, event)"
                        title="Edit Card">
                    <i class="bi bi-pencil text-secondary"></i>
                </button>
            </c:if>
        </div>

        <!-- Due Date -->
        <c:if test="${not empty card.due_date}">
            <c:choose>
                <c:when test="${card.due_date.time lt now.time}">
                    <div class="small mb-2 text-danger fw-bold ${card.completed ? 'opacity-75' : 'text-danger'}">
                        <i class="bi bi-clock me-1"></i> Due: 
                        <fmt:formatDate value="${card.due_date}" pattern="MMM d, yyyy" />
                    </div>
                </c:when>
                <c:when test="${(card.due_date.time - now.time) le (24*60*60*1000)}">
                    <div class="small mb-2 text-warning fw-bold ${card.completed ? 'opacity-75' : 'text-warning'}">
                        <i class="bi bi-clock me-1"></i> Due: 
                        <fmt:formatDate value="${card.due_date}" pattern="MMM d, yyyy" />
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="small mb-2 text-success ${card.completed ? 'opacity-75' : 'text-success'}">
                        <i class="bi bi-clock me-1"></i> Due: 
                        <fmt:formatDate value="${card.due_date}" pattern="MMM d, yyyy" />
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- Info Row -->
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
            <div class="d-flex flex-column gap-1 small text-muted ${card.completed ? 'opacity-75' : ''}">
                <!-- Comment count -->
                <c:if test="${not empty card.comments}">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-chat-left-text me-1"></i>${fn:length(card.comments)}
                    </div>
                </c:if>

                <!-- Attachment count -->
                <c:if test="${not empty card.attachments}">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-paperclip me-1"></i>${fn:length(card.attachments)}
                    </div>
                </c:if>

                <!-- Checklist progress -->
                <c:set var="totalItems" value="0" />
                <c:set var="completedItems" value="0" />
                <c:if test="${not empty card.checklists}">
                    <c:forEach var="cl" items="${card.checklists}">
                        <c:set var="totalItems" value="${totalItems + fn:length(cl.items)}" />
                        <c:forEach var="item" items="${cl.items}">
                            <c:if test="${item.completed}">
                                <c:set var="completedItems" value="${completedItems + 1}" />
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>
                <c:if test="${totalItems > 0}">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-check2-square me-1"></i>${completedItems}/${totalItems}
                    </div>
                </c:if>
            </div>

            <!-- Members -->
<%--             <c:set var="cardMembers" value="${cardMembersMap[card.id]}" />
 --%>				 <c:if test="${not empty card.members}">
                    <div class="card-members d-flex gap-1 mt-2">
                        <c:forEach var="member" items="${card.members}">
                            <c:set var="colorIndex" value="${(member.id % 8) + 1}" />
                            <span class="member-avatar member-color-${colorIndex}" title="${member.username}">
                                ${fn:toUpperCase(fn:substring(member.username, 0, 1))}
                            </span>
                        </c:forEach>
                    </div>
                </c:if>

        </div>

        <!-- Progress bar -->
        <c:if test="${totalItems > 0}">
            <div class="progress mt-2" style="height: 6px;">
                <div class="progress-bar bg-success" role="progressbar"
                     style="width: ${completedItems * 100 / totalItems}%;"
                     aria-valuenow="${completedItems}" 
                     aria-valuemin="0" aria-valuemax="${totalItems}">
                </div>
            </div>
        </c:if>
        
        <!-- Completion/Reopen button -->
        <div class="card-actions mt-2 pt-2 border-top">
            <form action="${pageContext.request.contextPath}/card/${card.id}/toggle-completion" 
                  method="post" class="d-inline">
                <button type="submit" class="btn btn-sm ${card.completed ? 'btn-outline-secondary' : 'btn-outline-success'}"
                        onclick="event.stopPropagation();">
                    <c:choose>
                        <c:when test="${card.completed}">
                            <i class="bi bi-arrow-counterclockwise me-1"></i>Reopen
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-check-circle me-1"></i>Complete
                        </c:otherwise>
                    </c:choose>
                </button>
            </form>
        </div>
    </div>

    <!-- Edit Card Modal (only show if not completed) -->
    <c:if test="${not card.completed}">
        <div class="modal fade" id="editCardModal${card.id}" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content bg-dark text-light">
                    <div class="modal-header">
                        <h5 class="modal-title">Edit Card: ${card.title}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/card/${card.id}/edit">
                        <div class="modal-body">
                            <input type="hidden" name="cardId" value="${card.id}" />
                            <div class="mb-3">
                                <label class="form-label">Title</label>
                                <input type="text" name="title" class="form-control bg-dark text-light border-secondary" 
                                       value="${card.title}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Description</label>
                                <textarea name="description" class="form-control bg-dark text-light border-secondary" 
                                          rows="3">${card.description}</textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-success">Save Changes</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Card Detail Modal -->
<div class="modal fade card-detail-modal" id="cardDetailModal${card.id}" tabindex="-1">
    <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content bg-dark text-white rounded-4 shadow-lg border border-secondary">
            
            <!-- Header -->
            <div class="modal-header border-secondary">
                <h5 class="modal-title d-flex align-items-center gap-2">
                    <i class="bi bi-card-text text-info"></i>
                    <span>${card.title}</span>
                    <c:if test="${card.completed}">
                        <span class="badge bg-success ms-2">
                            <i class="bi bi-check-circle-fill me-1"></i> Completed
                        </span>
                    </c:if>
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <!-- Body -->
            <div class="modal-body d-flex flex-lg-row flex-column gap-4 p-4" style="min-height: 75vh;">
                
                <!-- Left Column -->
                <div class="flex-grow-1 overflow-auto pe-2 border-end border-secondary">
                    
                    <!-- Completion Info -->
                    <c:if test="${card.completed}">
                        <div class="alert alert-info d-flex align-items-center gap-2">
                            <i class="bi bi-check-circle-fill"></i>
                            Completed on 
                            <fmt:formatDate value="${card.completedAt}" pattern="MMM d, yyyy 'at' h:mm a"/>
                        </div>
                    </c:if>

                    <!-- Description -->
                    <h6 class="fw-semibold mb-1">Description</h6>
                    <p class="text-light small">${card.description}</p>

                    <!-- Due Date -->
                    <c:if test="${not card.completed}">
                        <form action="${pageContext.request.contextPath}/card/${card.id}/updateDueDate" method="post" class="mb-4">
                            <label for="dueDate${card.id}" class="form-label fw-semibold">Due Date</label>
                            <div class="d-flex gap-2">
                                <input type="date" id="dueDate${card.id}" name="dueDate" 
                                       class="form-control bg-dark text-light border-secondary"
                                       value="${card.due_date}" />
                                <button type="submit" class="btn btn-outline-success btn-sm">Update</button>
                            </div>
                        </form>
                    </c:if>

                    <!-- Add Checklist -->
                    <c:if test="${not card.completed}">
                            <div id="showAddChecklistBtn${card.id}">
                                <button class="btn btn-sm btn-outline-info mt-3"
                                        type="button"
                                        onclick="toggleAddChecklistForm(${card.id})">
                                    + Add Checklist
                                </button>
                            </div>
                            <!-- Add Checklist Form (Initially Hidden) -->
                            <div id="addChecklistForm${card.id}" style="display: none;">
                                <form action="${pageContext.request.contextPath}/checklist/add" method="post" class="mb-3 mt-2">
                                    <input type="hidden" name="cards_id" value="${card.id}" />
                                    <div class="input-group mb-2">
                                        <input type="text" name="title" class="form-control bg-dark text-light border-secondary"
                                               placeholder="Enter checklist title..." required />
                                    </div>
                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-sm btn-success">Add</button>
                                        <button type="button" class="btn btn-sm btn-secondary" onclick="cancelAddChecklist(${card.id})">Cancel</button>
                                    </div>
                                </form>
                            </div>
                        </c:if>

                    <!-- Checklists -->
                    <c:forEach var="checklist" items="${card.checklists}">
                        <div class="mt-4 p-3 rounded-3 border border-secondary bg-dark shadow-sm">
                            
                            <!-- Checklist Header -->
                            <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center gap-2">
                                    <i class="bi bi-list-check text-info"></i>
                                    <h6 class="fw-semibold mb-0">${checklist.title}</h6>
                                </div>
                                <div class="d-flex gap-2">
                                    <!-- Collapse Toggle -->
									<button class="btn btn-sm toggle-btn toggle-btn-white"
									        type="button"
									        data-bs-toggle="collapse"
									        data-bs-target="#checklistItems${checklist.id}"
									        aria-expanded="true"
									        title="Show/Hide Items">
									    <i class="bi bi-chevron-down"></i>
									</button>
									
									<!-- Edit Checklist -->
									<button class="btn btn-sm text-primary toggle-btn"
									        type="button"
									        data-bs-toggle="collapse"
									        data-bs-target="#editChecklist${checklist.id}"
									        aria-expanded="false"
									        title="Edit Checklist">
									    <i class="bi bi-pencil"></i>
									</button>
									
									<!-- Delete Checklist -->
									<form action="${pageContext.request.contextPath}/checklist/delete" method="post" class="m-0">
									    <input type="hidden" name="checklists_id" value="${checklist.id}" />
									    <button type="submit" class="btn btn-sm text-danger toggle-btn" title="Delete Checklist">
									        <i class="bi bi-trash"></i>
									    </button>
									</form>

                                </div>
                            </div>

                            <!-- Edit Checklist -->
                            <div class="collapse mt-2" id="editChecklist${checklist.id}">
                                <form action="${pageContext.request.contextPath}/checklist/update" method="post" class="d-flex gap-2">
                                    <input type="hidden" name="checklists_id" value="${checklist.id}" />
                                    <input type="text" name="title" value="${checklist.title}"
                                           class="form-control bg-dark text-light border-secondary" required />
                                    <button type="submit" class="btn btn-success btn-sm">Save</button>
                                </form>
                            </div>

                            <!-- Progress Bar -->
                            <c:set var="total" value="${fn:length(checklist.items)}" />
                            <c:set var="completed" value="0" />
                            <c:forEach var="item" items="${checklist.items}">
                                <c:if test="${item.completed}">
                                    <c:set var="completed" value="${completed + 1}" />
                                </c:if>
                            </c:forEach>
                            <c:set var="percentage" value="${total > 0 ? (completed * 100 / total) : 0}" />

                            <div class="progress rounded-3 my-2" style="height: 8px;">
                                <div class="progress-bar ${percentage == 100 ? 'bg-success' : 'bg-info'}"
                                     role="progressbar"
                                     style="width: ${percentage}%"
                                     aria-valuenow="${percentage}" aria-valuemin="0" aria-valuemax="100">
                                </div>
                            </div>
                            <small class="text-muted">${completed} of ${total} completed</small>

                            <!-- Checklist Items -->
                            <div class="collapse show mt-2" id="checklistItems${checklist.id}">
                                <ul class="list-group list-group-flush rounded-3 overflow-hidden">
                                    <c:forEach var="item" items="${checklist.items}">
                                        <li class="list-group-item bg-dark text-light d-flex justify-content-between align-items-center">
                                            <div class="d-flex align-items-center gap-2">
                                                <form action="${pageContext.request.contextPath}/checklist/item/toggle"
                                                      method="post" class="m-0">
                                                    <input type="hidden" name="item_id" value="${item.id}" />
                                                    <input type="hidden" name="checklists_id" value="${checklist.id}" />
                                                    <input type="checkbox" class="form-check-input"
                                                           onchange="this.form.submit()"
                                                           <c:if test="${item.completed}">checked</c:if>
                                                           <c:if test="${card.completed}">disabled</c:if> />
                                                </form>
                                                <span>${item.text}</span>
                                            </div>

                                            <!-- Actions -->
                                            <div class="d-flex gap-2">
                                               <!-- Edit Item -->
							<button class="btn btn-sm btn-icon text-primary"
							        type="button"
							        data-bs-toggle="collapse"
							        data-bs-target="#editItem${item.id}">
							    <i class="bi bi-pencil"></i>
							</button>
							
							<!-- Delete Item -->
							<form action="${pageContext.request.contextPath}/checklist/item/delete" method="post" class="m-0">
							    <input type="hidden" name="item_id" value="${item.id}" />
							    <input type="hidden" name="checklists_id" value="${checklist.id}" />
							    <button type="submit" class="btn btn-sm btn-icon text-danger">
							        <i class="bi bi-trash"></i>
							    </button>
							</form>

                                            </div>
                                        </li>

                                        <!-- Edit Item -->
                                        <div class="collapse mt-2" id="editItem${item.id}">
                                            <form action="${pageContext.request.contextPath}/checklist/item/update"
                                                  method="post" class="d-flex gap-2">
                                                <input type="hidden" name="item_id" value="${item.id}" />
                                                <input type="hidden" name="checklists_id" value="${checklist.id}" />
                                                <input type="text" name="text" value="${item.text}"
                                                       class="form-control bg-dark text-light border-secondary" required />
                                                <button type="submit" class="btn btn-success btn-sm">Save</button>
                                            </form>
                                        </div>
                                    </c:forEach>
                                </ul>

                                <!-- Add Item -->
                                <c:if test="${not card.completed}">
                                    <button class="btn btn-outline-info btn-sm mt-2 w-100"
                                            data-bs-toggle="collapse"
                                            data-bs-target="#addItemForm${checklist.id}">
                                        <i class="bi bi-plus-lg"></i> Add Item
                                    </button>
                                    <div class="collapse mt-2" id="addItemForm${checklist.id}">
                                        <form action="${pageContext.request.contextPath}/checklist/item/add"
                                              method="post" class="d-flex gap-2">
                                            <input type="hidden" name="checklists_id" value="${checklist.id}" />
                                            <input type="text" name="text" placeholder="New checklist item..."
                                                   class="form-control bg-dark text-light border-secondary" required />
                                            <button type="submit" class="btn btn-success btn-sm">Add</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>

                    <!-- File Upload -->
                    <c:if test="${not card.completed}">
                        <form action="${pageContext.request.contextPath}/card/${card.id}/uploadAttachment"
                              method="post" enctype="multipart/form-data" class="mt-4">
                            <label class="form-label fw-semibold">Attachments</label>
                            <input type="file" name="file" class="form-control bg-dark text-light border-secondary" />
                            <button type="submit" class="btn btn-outline-info btn-sm mt-2">Upload</button>
                        </form>
                    </c:if>

                    <!-- Existing Attachments -->
                    <c:if test="${not empty card.attachments}">
                        <h6 class="mt-3">Attachments:</h6>
                        <ul class="list-group list-group-flush">
                            <c:forEach var="att" items="${card.attachments}">
                                <li class="list-group-item bg-dark text-light">
                                    <a href="${pageContext.request.contextPath}/attachments/${att.id}/download" class="text-info">
                                        <i class="bi bi-paperclip"></i> ${att.filename}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>

                    <!-- Assign Members -->
                    <c:if test="${not card.completed}">
                        <form action="${pageContext.request.contextPath}/card/${card.id}/assign" method="post" class="mt-4">
                            <label class="fw-semibold mb-2">Assign Members</label>
                            <div class="dropdown">
                                <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">Choose Member</button>
                                <ul class="dropdown-menu dropdown-menu-dark">
                                    <c:forEach var="member" items="${boardMembers}">
                                        <li>
                                            <button type="submit" name="userId" value="${member.user.id}" class="dropdown-item">
                                                ${member.user.username}
                                            </button>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </form>
                    </c:if>

                    <!-- Current Members -->
<%--                     <c:set var="modalCardMembers" value="${cardMembersMap[card.id]}" />
 --%>                    <c:if test="${not empty card.members}">
					    <h6 class="mt-3">Members:</h6>
					    <c:forEach var="cm" items="${card.members}">
					        <form action="${pageContext.request.contextPath}/card/${card.id}/remove"
					              method="post" class="d-inline">
					            <input type="hidden" name="userId" value="${cm.id}" />
					            <span class="badge bg-secondary me-2">
					                ${cm.username}
					                <c:if test="${not card.completed}">
					                    <button type="submit" 
					                            class="btn-close btn-close-white btn-sm ms-1" 
					                            style="font-size: 0.6rem;">
					                    </button>
					                </c:if>
					            </span>
					        </form>
					    </c:forEach>
					</c:if>

                </div>

                <!-- Right Column (Comments) -->
                <div class="flex-grow-1 overflow-auto ps-2">
                    <h6 class="fw-semibold mb-3">Comments</h6>
                    <c:if test="${not card.completed}">
                        <form action="${pageContext.request.contextPath}/card/${card.id}/comment" method="post" class="mb-3">
                            <textarea name="content" class="form-control bg-dark text-light border-secondary mb-2" rows="3" placeholder="Write a comment..." required></textarea>
                            <button type="submit" class="btn btn-outline-light btn-sm">Add</button>
                        </form>
                    </c:if>
                    <ul class="list-group list-group-flush">
                        <c:forEach var="comment" items="${card.comments}">
                            <li class="list-group-item bg-dark text-light border-secondary">
                                <p class="mb-1">${comment.content}</p>
                                <small class="text-info">by ${comment.user.username}</small><br/>
                                <small class="text"><fmt:formatDate value="${comment.createdAt}" pattern="MMM d, yyyy h:mm a" /></small>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>

            <!-- Footer -->
            
				<div class="modal-footer border-secondary d-flex justify-content-end px-4 py-1">
				    <form action="${pageContext.request.contextPath}/card/${card.id}/toggle-completion" method="post" class="m-0">
				        <button type="submit" 
				                class="btn btn-sm ${card.completed ? 'btn-outline-warning' : 'btn-success'} d-flex align-items-center gap-2 px-3 py-1 rounded-3 shadow-sm">
				            <c:choose>
				                <c:when test="${card.completed}">
				                    <i class="bi bi-arrow-counterclockwise"></i> Reopen
				                </c:when>
				                <c:otherwise>
				                    <i class="bi bi-check-circle"></i> Complete
				                </c:otherwise>
				            </c:choose>
				        </button>
				    </form>
				</div>



        </div>
    </div>
</div>

</c:forEach>



            <!-- Add Card Button -->
            <button class="btn btn-sm btn-outline-light w-100 mt-2" 
                    data-bs-toggle="modal" 
                    data-bs-target="#addCardModal${list.id}">
                + Add Card
            </button>
        </div>

        <!-- Add Card Modal -->
        <div class="modal fade" id="addCardModal${list.id}" tabindex="-1" aria-labelledby="addCardLabel${list.id}" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content bg-dark text-light">
                    <form action="${pageContext.request.contextPath}/card/add" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title">Add Card to ${list.title}</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="listId" value="${list.id}" />
                            <div class="mb-3">
                                <label class="form-label">Title</label>
                                <input type="text" name="title" class="form-control bg-dark text-light" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Description</label>
                                <textarea name="description" class="form-control bg-dark text-light"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary">Add Card</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </form>
                </div>
            </div>
            
            
        </div>

        <!-- Edit List Modal -->
        <div class="modal fade" id="editListModal${list.id}" tabindex="-1" aria-labelledby="editListLabel${list.id}" aria-hidden="true">
            <div class="modal-dialog">
                <form action="${pageContext.request.contextPath}/list/update" method="post">
                    <div class="modal-content bg-dark text-light">
                        <div class="modal-header">
                            <h5 class="modal-title">Edit List Title</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="listId" value="${list.id}" />
                            <div class="mb-3">
                                <label class="form-label">New Title</label>
                                <input type="text" name="title" class="form-control bg-dark text-light" value="${list.title}" required />
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-success">Save Changes</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </c:forEach>
    
    <!-- Add List Button -->
    <c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
        <div class="add-list-btn" data-bs-toggle="modal" data-bs-target="#addListModal">
            <span class="fw-semibold">+ Add List</span>
        </div>
    </c:if>
    
    <!-- Add List Modal -->
    <div class="modal fade" id="addListModal" tabindex="-1" aria-labelledby="addListLabel" aria-hidden="true">
        <div class="modal-dialog">
            <form action="${pageContext.request.contextPath}/list/add" method="post">
                <div class="modal-content bg-black text-white">
                    <div class="modal-header border-0">
                        <h5 class="modal-title">Add New List</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="boardId" value="${board.id}" />
                        <div class="mb-3">
                            <label class="form-label text-white">List Title</label>
                            <input type="text" name="title" class="form-control bg-dark text-white border-light" required />
                        </div>
                    </div>
                    <div class="modal-footer border-0">
                        <button type="submit" class="btn btn-outline-light">Add List</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

    
</div>

<!-- Add Checklist Modal -->
<div class="modal fade" id="addChecklistModal${card.id}" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content bg-dark text-white rounded-3">
            <div class="modal-header border-bottom">
                <h5 class="modal-title">Add Checklist to "${card.title}"</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/checklist/add" method="post">
                <div class="modal-body">
                    <input type="hidden" name="cards_id" value="${card.id}" />
                    <div class="mb-3">
                        <label class="form-label">Checklist Title</label>
                        <input type="text" name="title"
                               class="form-control bg-dark text-light border-secondary"
                               placeholder="Enter checklist title..."
                               required />
                    </div>
                </div>
                <div class="modal-footer border-top">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Add</button>
                </div>
            </form>
        </div>
    </div>
</div>


<!-- Add member modal -->
<div class="modal fade" id="addMemberModal-${board.id}" tabindex="-1">
    <div class="modal-dialog">
        <form class="modal-content bg-dark text-white" action="${pageContext.request.contextPath}/board/add-member" method="post">
            <div class="modal-header border-secondary">
                <h5 class="modal-title">Add Member to ${board.title}</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <div class="modal-body">
                <input type="hidden" name="boardId" value="${board.id}">
                <input type="hidden" name="workspaceId" value="${workspace.id}">

                <div class="mb-3">
                    <label class="form-label text-white">Select Member</label>
                    <select name="userId" class="form-select bg-dark text-white border-secondary" required>
                        <option value="" disabled selected>Choose member</option>
                        <c:forEach items="${workspaceMembers}" var="member">
                            <option value="${member.id}">
                                ${member.username} (${member.email})
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label text-white">Role</label>
                    <select name="role" class="form-select bg-dark text-white border-secondary" required>
                        <option value="member">Member</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>
            </div>

            <div class="modal-footer border-secondary">
                <button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-outline-light">Add Member</button>
            </div>
        </form>
    </div>
</div>

<!-- Activity History Sidebar -->
<div class="offcanvas offcanvas-end bg-dark text-light" tabindex="-1" id="activitySidebar">
  <div class="offcanvas-header border-bottom border-secondary">
    <h5 class="offcanvas-title">Activity</h5>
    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
  </div>
  <div class="offcanvas-body">
    <c:if test="${not empty boardActivities}">
      <ul class="list-group list-group-flush">
        <c:forEach var="activity" items="${boardActivities}">
          
          <!-- Determine icon based on action -->
          <c:set var="activityIcon" value="bi bi-circle" />
          <c:choose>
            <c:when test="${fn:contains(activity.action, 'comment')}">
              <c:set var="activityIcon" value="bi bi-chat-text" />
            </c:when>
            <c:when test="${fn:contains(activity.action, 'attachment')}">
              <c:set var="activityIcon" value="bi bi-paperclip" />
            </c:when>
            <c:when test="${fn:contains(activity.action, 'member')}">
              <c:set var="activityIcon" value="bi bi-person" />
            </c:when>
            <c:when test="${fn:contains(activity.action, 'checklist')}">
              <c:set var="activityIcon" value="bi bi-check2-square" />
            </c:when>
            <c:when test="${fn:contains(activity.action, 'due date')}">
              <c:set var="activityIcon" value="bi bi-calendar-event" />
            </c:when>
          </c:choose>

          <!-- Activity item -->
          <li class="list-group-item bg-dark text-light border-secondary d-flex align-items-start">
				  <!-- Circle wrapper -->
				  <div class="icon-circle me-2 
				        <c:choose>
				          <c:when test="${fn:contains(activity.action, 'comment')}">bg-info</c:when>
				          <c:when test="${fn:contains(activity.action, 'attachment')}">bg-warning</c:when>
				          <c:when test="${fn:contains(activity.action, 'member')}">bg-success</c:when>
				          <c:when test="${fn:contains(activity.action, 'checklist')}">bg-primary</c:when>
				          <c:when test="${fn:contains(activity.action, 'due date')}">bg-danger</c:when>
				          <c:otherwise>bg-secondary</c:otherwise>
				        </c:choose>">
				    <i class="${activityIcon} text-white"></i>
				  </div>
				
				  <!-- Text content -->
				  <div>
				    <strong>${activity.user.username}</strong> ${activity.action}
				    <br>
				    <span style="color:gray;">
				      <small><fmt:formatDate value="${activity.createdAt}" pattern="MMM d, yyyy h:mm a" /></small>
				    </span>
				  </div>
				</li>


        </c:forEach>
      </ul>
      
      
      
    </c:if>

    <c:if test="${empty boardActivities}">
      <p class="text">No activities yet.</p>
    </c:if>
    
  </div>
</div>




<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.15.0/Sortable.min.js"></script>



<script>
    function toggleAddChecklistForm(cardId) {
        document.getElementById('showAddChecklistBtn' + cardId).style.display = 'none';
        document.getElementById('addChecklistForm' + cardId).style.display = 'block';
    }

    function cancelAddChecklist(cardId) {
        document.getElementById('addChecklistForm' + cardId).style.display = 'none';
        document.getElementById('showAddChecklistBtn' + cardId).style.display = 'block';
    }
    
    document.addEventListener('DOMContentLoaded', function() {
        // Get card ID from URL
        const urlParams = new URLSearchParams(window.location.search);
        const cardId = urlParams.get('card');
        
        if (cardId) {
            // Show the modal
            const modal = new bootstrap.Modal(document.getElementById('cardDetailModal' + cardId));
            modal.show();
            
            // Clean URL after modal is shown (optional)
            history.replaceState(null, null, window.location.pathname);
        }
    });
</script>

<c:if test="${not empty boardError}">
    <script>
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: '${boardError}',
            confirmButtonColor: '#d33'
        });
    </script>
</c:if>

<c:if test="${not empty boardSuccess}">
    <script>
        Swal.fire({
            icon: 'success',
            title: 'Success',
            text: '${boardSuccess}',
            confirmButtonColor: '#3085d6'

        });
    </script>
</c:if>

<script>
function confirmDeleteItem(itemText) {
    return confirm("Are you sure you want to delete this item?\n\n" + itemText);
}
function confirmDeleteChecklist(checklistTitle) {
    return confirm("Are you sure you want to delete this checklist?\n\n" + checklistTitle + "\n\n⚠️ All its items will also be deleted!");
}
</script>


<script>
function handleCardClick(cardId, isCompleted) {
    if (!isCompleted) {
        openCardDetail(cardId);
    }
}

function openCardDetail(cardId) {
    const modal = new bootstrap.Modal(document.getElementById('cardDetailModal' + cardId));
    modal.show();
}

function openEditModal(cardId, event) {
    event.stopPropagation();
    const modal = new bootstrap.Modal(document.getElementById('editCardModal' + cardId));
    modal.show();
}

function toggleAddChecklistForm(cardId) {
    const form = document.getElementById('addChecklistForm' + cardId);
    const button = document.getElementById('showAddChecklistBtn' + cardId);
    
    if (form.style.display === 'none') {
        form.style.display = 'block';
        button.style.display = 'none';
    } else {
        form.style.display = 'none';
        button.style.display = 'block';
    }
}

function cancelAddChecklist(cardId) {
    document.getElementById('addChecklistForm' + cardId).style.display = 'none';
    document.getElementById('showAddChecklistBtn' + cardId).style.display = 'block';
}
</script>

<script>
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".card-list").forEach(function (listEl) {
        new Sortable(listEl, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            onEnd: function (evt) {
                let cardIds = Array.from(evt.to.children)
                                   .map(card => card.getAttribute("data-card-id"));

                // Send new order to backend
                fetch("${pageContext.request.contextPath}/card/reorder", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ listId: evt.to.id.replace("cardList", ""), cardIds: cardIds })
                });
            }
        });
    });
});


</script>



</body>
</html>
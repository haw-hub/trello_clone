<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Workspace Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #1e1e2f;
            color: white;
        }
        .workspace-card {
            background-color: #2c2c3e;
            border-radius: 8px;
            padding: 15px;
            color: #eee;
            border: 1px solid #3a3a4d;
            transition: all 0.2s ease-in-out;
            position: relative;
            min-height: 130px;
        }
        .workspace-card:hover {
            border-color: darkcyan;
            box-shadow: 0 0 12px rgba(0, 139, 139, 0.5);
            transform: translateY(-3px);
        }
        .workspace-card h6 {
            margin: 0;
            font-size: 1rem;
            font-weight: 600;
            color: white;
        }
        .workspace-card p {
            font-size: 0.85rem;
            color: #bbb;
            margin-bottom: 0.5rem;
            min-height: 40px;
        }
        .workspace-badge {
            font-size: 0.7rem;
            background-color: darkcyan;
            color: white;
            border-radius: 4px;
            padding: 2px 6px;
        }
        .workspace-menu {
            position: absolute;
            top: 10px;
            right: 10px;
        }
        /* 🔹 My Assignments Styling */
        .assignments-section .list-group-item {
            background-color: #2c2c3e;
            border: 1px solid #3a3a4d;
            color: #eee;
            border-radius: 6px;
            margin-bottom: 8px;
        }
        .assignments-section .list-group-item:hover {
            border-color: darkcyan;
            box-shadow: 0 0 8px rgba(0, 139, 139, 0.4);
        }
        .assignments-section strong {
            color: #fff;
        }
        .assignments-section small {
            color: #bbb;
        }
        /* 🔹 Completed Task */
        .completed {
            text-decoration: line-through;
            color: #999 !important;
        }
        /* 🔹 Due date colors */
        .due-overdue {
		    color: #ff6b6b !important;   /* red */
		    font-weight: bold;
		}
		.due-today {
		    color: #ffa94d !important;   /* orange */
		    font-weight: bold;
		}
		.due-upcoming {
		    color: #69db7c !important;   /* green */
		    font-weight: bold;
		}
		.text-muted, .no-due {
		    color: #bbb !important;      /* gray */
		}
		.completed {
    text-decoration: line-through;
    color: #999 !important;
}
a.view {
    display: inline-flex;           /* align icon + text */
    align-items: center;
    gap: 6px;                       /* space between icon and text */
    padding: 2px 6px;
    background-color: gray;      /* bootstrap blue */
    color: #fff !important;
    text-decoration: none;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 500;
    transition: background 0.2s ease, transform 0.1s ease;
}

a.view:hover {
    background-color: darkcyan;      /* darker blue on hover */
    transform: translateY(-1px);    /* slight lift effect */
}

a.view:active {
    transform: translateY(0);       /* reset when clicked */
}

		

    </style>
</head>
<body>
    <%@ include file="navbar.jsp" %>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <%@ include file="Sidebar.jsp" %>

            <!-- Main Content -->
            <div class="col-md-10 p-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4>Your Workspaces</h4>
                    <button type="button" class="btn btn-outline-info" data-bs-toggle="modal" data-bs-target="#createWorkspaceModal">
                        <i class="fas fa-plus me-2"></i> Create Workspace
                    </button>
                </div>

                <!-- Admin Workspaces -->
                <h5 class="text-secondary mb-2">Admin Workspaces</h5>
                <div class="row g-3 mb-4">
                    <c:if test="${empty adminWorkspaces}">
                        <div class="col-12"><span class="text">No admin workspaces</span></div>
                    </c:if>
                    <c:forEach var="ws" items="${adminWorkspaces}">
                        <div class="col-md-3 col-sm-6">
                            <div class="workspace-card h-100">
                                <!-- Dropdown Menu -->
                                <div class="dropdown workspace-menu">
                                    <button class="btn btn-sm btn-outline-light" type="button" id="dropdown${ws.id}" data-bs-toggle="dropdown">
                                        <i class="fas fa-ellipsis-v"></i>
                                    </button>
                                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end" aria-labelledby="dropdown${ws.id}">
                                        <li>
                                            <a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#editWorkspaceModal${ws.id}">
                                                <i class="fas fa-edit me-2"></i>Edit
                                            </a>
                                        </li>
                                        <li>
                                            <form action="${pageContext.request.contextPath}/workspace/delete" 
											      method="post" 
											      onsubmit="return confirm('Are you sure you want to delete this workspace?');">
											    <input type="hidden" name="workspaceId" value="${ws.id}" />
											    <button type="submit" class="dropdown-item text-danger">
											        <i class="bi bi-trash me-2"></i>Delete Workspace
											    </button>
											</form>

                                        </li>
                                    </ul>
                                </div>

                                <!-- Workspace Info -->
                                <a href="${pageContext.request.contextPath}/workspace/${ws.id}" class="text-decoration-none">
                                    <h6><i class="fas fa-briefcase me-2"></i> ${ws.name}</h6>
                                    <p>
                                        <c:choose>
                                            <c:when test="${not empty ws.description}">${ws.description}</c:when>
                                            <c:otherwise>No description</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <span class="workspace-badge">Admin</span>
                                </a>
                            </div>
                        </div>

                        <!-- Edit Workspace Modal -->
                        <div class="modal fade" id="editWorkspaceModal${ws.id}" tabindex="-1" aria-labelledby="editWorkspaceLabel${ws.id}" aria-hidden="true">
                          <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content text-white" style="background-color:#2c2c3e;">
                              <div class="modal-header">
                                <h5 class="modal-title" id="editWorkspaceLabel${ws.id}">Edit Workspace</h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                              </div>
                              <form method="post" action="${pageContext.request.contextPath}/workspace/update">
                                <div class="modal-body">
                                    <input type="hidden" name="workspaceId" value="${ws.id}" />
                                  <div class="mb-3">
                                    <label class="form-label">Workspace Name</label>
                                    <input type="text" class="form-control" name="name" value="${ws.name}" required />
                                  </div>
                                  <div class="mb-3">
                                    <label class="form-label">Description</label>
                                    <textarea class="form-control" name="description" rows="3">${ws.description}</textarea>
                                  </div>
                                </div>
                                <div class="modal-footer">
                                  <button type="submit" class="btn btn-success">Save</button>
                                  <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                </div>
                              </form>
                            </div>
                          </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Member Workspaces -->
                <h5 class="text-secondary mb-2">Member Workspaces</h5>
                <div class="row g-3">
                    <c:if test="${empty memberWorkspaces}">
                        <div class="col-12"><span class="text">No member workspaces</span></div>
                    </c:if>
                    <c:forEach var="ws" items="${memberWorkspaces}">
                        <div class="col-md-3 col-sm-6">
                            <div class="workspace-card h-100">
                                <a href="${pageContext.request.contextPath}/workspace/${ws.id}" class="text-decoration-none">
                                    <h6><i class="fas fa-users me-2"></i> ${ws.name}</h6>
                                    <p>
                                        <c:choose>
                                            <c:when test="${not empty ws.description}">${ws.description}</c:when>
                                            <c:otherwise>No description</c:otherwise>
                                        </c:choose>
                                    </p>
                                    <span class="workspace-badge">Member</span>
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- 🔹 My Assignments Section -->
                <div class="assignments-section mt-5">
                  <h5 class="mb-3"><i class="fas fa-tasks me-2"></i>My Assignments</h5>
                  <c:set var="today" value="<%= new java.util.Date() %>" />
                  <c:choose>
                    <c:when test="${not empty myAssignments}">
                      <ul class="list-group">
                        <c:forEach var="card" items="${myAssignments}">
                          <li class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                              <strong class="<c:if test='${card.completed}'>completed</c:if>">${card.title}</strong><br>
                              <small>Board: ${card.list.board.title} | List: ${card.list.title}</small><br>
                              
                              <!-- 🔹 Due Date -->
                              <c:choose>
                                <c:when test="${empty card.due_date}">
                                  <small class="text-muted">No due date</small>
                                </c:when>
                                <c:when test="${card.due_date.time lt today.time}">
                                  <small class="due-overdue">⚠ Overdue (<fmt:formatDate value='${card.due_date}' pattern='dd MMM yyyy'/>)</small>
                                </c:when>
                                <c:when test="${fn:substring(card.due_date,0,10) eq fn:substring(today,0,10)}">
                                  <small class="due-today">📅 Due Today</small>
                                </c:when>
                                <c:otherwise>
                                  <small class="due-upcoming">⏳ Due <fmt:formatDate value='${card.due_date}' pattern='dd MMM yyyy'/></small>
                                </c:otherwise>
                              </c:choose>
                            </div>
                            
                            <!-- Actions -->
                            <c:choose>
                              <c:when test="${card.completed}">
                                <span class="badge bg-success">✅ Done</span>
                              </c:when>
                              <c:otherwise>
                                <div>
                                  <a class="view" href="${pageContext.request.contextPath}/board/${card.list.board.id}"><i class="fas fa-eye me-2"></i>View</a>
                                  
                                </div>
                              </c:otherwise>
                            </c:choose>
                          </li>
                        </c:forEach>
                      </ul>
                    </c:when>
                    <c:otherwise>
                      <p class="text">You have no assigned tasks.</p>
                    </c:otherwise>
                  </c:choose>
                </div>
            </div>
        </div>
    </div>

    <!-- Create Workspace Modal -->
    <div class="modal fade" id="createWorkspaceModal" tabindex="-1" aria-labelledby="createWorkspaceLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content text-white" style="background-color:#2c2c3e;">
          <div class="modal-header">
            <h5 class="modal-title" id="createWorkspaceLabel">Create Workspace</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
          </div>
          <form method="post" action="${pageContext.request.contextPath}/workspace/create">
            <div class="modal-body">
              <div class="mb-3">
                <label for="name" class="form-label">Workspace Name</label>
                <input type="text" class="form-control" id="name" name="name" required />
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="submit" class="btn btn-primary">Create</button>
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

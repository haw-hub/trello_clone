<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE html>
<html>
<head>
    <title>${workspace.name} - Boards</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        body {
            background-color: #1e1e2f;
            color: white;
            
        }
        
        .board-card {
            background-color: #2c2c3e;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 10px;
        }
        .viewBoard{
        	border-color: darkcyan;
        	color:white;
        }
        .btn.text-danger:hover i {
		    color: white;
		}
        .dropdown-menu-dark li:hover {
		    background-color: rgba(255, 255, 255, 0.05); 
		    cursor: default;
		}
		.createboard{
			border-color: darkcyan;
        	color:white;
		}
        
        
    
    .board-grid {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
    }

    .board-card {
        background-color: #2c2c3e;
        border-radius: 12px;
        width: 220px;
        height: 100px;
        padding: 16px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        color: #f1f1f1;
        text-decoration: none;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.25);
        position: relative;
    }

    .board-card:hover {
        background-color: #3a3a4d;
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 188, 212, 0.4);
    }
    

    .board-card h6 {
        font-size: 1rem;
        font-weight: 600;
        margin: 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .board-card .board-icon {
        font-size: 1.5rem;
        color: #00bcd4;
        position: absolute;
        top: 12px;
        right: 12px;
    }

    .create-board-card {
        background-color: #1e1e2f;
        border: 2px dashed #555;
        color: #888;
        display: flex;
        align-items: center;
        justify-content: center;
        text-align: center;
        font-weight: 500;
        transition: all 0.2s ease;
    }

    .create-board-card:hover {
        background-color: #2a2a3a;
        color: #00bcd4;
        border-color: #00bcd4;
        transform: translateY(-3px);
    }

    

        
        
    </style>
</head>
<body>





        <%@ include file="navbar.jsp" %>
		
				


<div class="container-fluid">
    <div class="row">
        <%@ include file="Sidebar.jsp" %>

        <div class="col-md-10 p-4">
        	<div class="d-flex justify-content-between	">
        	<div>
            <h3>Workspace Name : ${workspace.name}</h3>
            </div>
           	<div class="d-flex">
           	
			<div class="dropdown mb-3">
				<!-- Trigger button (add data-bs-toggle) -->
				
			    <button class="btn btn-outline-light dropdown-toggle" type="button" id="memberDropdown" data-bs-toggle="dropdown" aria-expanded="false">
			        <i class="fas fa-users me-2"></i> Workspace Members (${members.size()})
			    </button>
			
			    <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="memberDropdown" style="min-width: 320px;">
			        <c:choose>
			            <c:when test="${not empty members}">
			                <c:forEach var="member" items="${members}">
			                    <li class="dropdown-item px-3 py-2 d-flex align-items-center justify-content-between">
			                        <div class="d-flex align-items-center flex-grow-1">
			                            <div class="me-3">
			                                <div class="fw-semibold text-truncate text-white" style="max-width: 150px;">
			                                    ${member.user.username}
			                                </div>
			                                <small class="text-secondary text-truncate" style="max-width: 150px;">
			                                    ${member.user.email}
			                                </small>
			                            </div>
			                        </div>
			
			                        <c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
			                            <div class="d-flex align-items-center ms-auto">
			                                <c:choose>
			                                    <c:when test="${member.role eq 'OWNER'}">
			                                        <span class="badge bg-info text-dark me-3">
			                                            <i class="fas fa-crown me-1"></i> Owner
			                                        </span>
			                                    </c:when>
			
			                                    <c:otherwise>
			                                        <form method="post" action="${pageContext.request.contextPath}/workspace/change-role"
			                                              class="d-flex align-items-center me-3"
			                                              onsubmit="return confirm('Are you sure you want to change this member\\'s role?');">
			                                            <input type="hidden" name="workspaceId" value="${member.workspace.id}" />
														<input type="hidden" name="userId" value="${member.user.id}" />

			
			                                            <div class="form-check form-switch p-0">
			                                                <input type="hidden" name="newRole" value="${member.role eq 'ADMIN' ? 'ADMIN' : 'MEMBER'}" />
			                                                <input class="form-check-input" type="checkbox" role="switch"
			                                                       id="roleSwitch-${member.id}"
			                                                       ${member.role eq 'ADMIN' ? 'checked' : ''}
			                                                       onchange="this.previousElementSibling.value=this.checked?'ADMIN':'MEMBER'; this.form.submit()" />
			                                                <label class="form-check-label text-light ms-2" for="roleSwitch-${member.id}">
			                                                    
			                                                    ${member.role eq 'ADMIN' ? ' Admin' : 'Member'}
			                                                </label>
			                                            </div>
			                                        </form>
			                                    </c:otherwise>	
			                                </c:choose>
			
			                                <c:if test="${member.role ne 'OWNER'}">
			                                    <form method="post" action="${pageContext.request.contextPath}/workspace/remove-member"
			                                          onsubmit="return confirm('Are you sure you want to remove ${member.user.username} from this workspace?');">
			                                         <input type="hidden" name="workspaceId" value="${member.workspace.id}" />
														<input type="hidden" name="userId" value="${member.user.id}" />

			                                        <button type="submit" class="btn btn-sm text-danger border-0 p-0" title="Remove Member">
			                                            <i class="fas fa-times-circle fs-5"></i>
			                                        </button>
			                                    </form>
			                                </c:if>
			                            </div>
			                        </c:if>
			                    </li>
			                </c:forEach>
			            </c:when>
			            <c:otherwise>
			                <li><span class="dropdown-item text-light text-center">No members in this workspace yet.</span></li>
			            </c:otherwise>
			        </c:choose>
			
			        <c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
			            <li class="px-3 py-2 border-top border-secondary">
			                <button class="btn w-100 text-white" style="background-color: darkcyan;" data-bs-toggle="modal" data-bs-target="#addMemberModal">
			                    <i class="fas fa-user-plus me-1"></i> Invite Member
			                </button>
			            </li>
			        </c:if>
			    </ul>
			</div>


			</div>

            </div>
   

            <h5 class="mt-4">Your Boards</h5>
<div class="board-grid mt-3">
    <c:forEach var="board" items="${boards}">
    <div class="board-card-wrapper d-inline-block m-2" style="position: relative;">
        
        <!-- Board Card -->
        <a href="${pageContext.request.contextPath}/board/${board.id}" 
           class="board-card text-white d-flex align-items-center justify-content-center"
           style="background-color: ${board.background}; 
                  width: 200px; 
                  height: 100px; 
                  border-radius: 8px; 
                  text-decoration: none; 
                  font-weight: bold;">
            <h6 class="m-0">${board.title}</h6>
        </a>
        
        <!-- Dropdown Menu (top-right of card) -->
        <div class="dropdown" style="position: absolute; top: 5px; right: 5px;">
            <button class="btn btn-sm btn-outline-light " type="button" data-bs-toggle="dropdown">
                ⋮
            </button>
            <ul class="dropdown-menu">
                <!-- Edit -->
                <li>
                    <a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#editBoardModal${board.id}">
                        Edit
                    </a>
                </li>
                <!-- Remove -->
                <li>
                    <form method="post" action="${pageContext.request.contextPath}/board/remove"
                          onsubmit="return confirm('Are you sure you want to delete this board?');">
                        <input type="hidden" name="boardId" value="${board.id}" />
                        <input type="hidden" name="workspaceId" value="${board.workspaces_id}" />
                        <button type="submit" class="dropdown-item text-danger">Remove</button>
                    </form>
                </li>
            </ul>
        </div>
        
        <!-- Edit Board Modal -->
<div class="modal fade" id="editBoardModal${board.id}" tabindex="-1" aria-labelledby="editBoardModalLabel${board.id}" aria-hidden="true">
  <div class="modal-dialog">
    <form action="${pageContext.request.contextPath}/board/edit" method="post">
      <div class="modal-content bg-dark text-light rounded-3 shadow-lg">
        
        <!-- Header -->
        <div class="modal-header border-secondary">
          <h5 class="modal-title" id="editBoardModalLabel${board.id}">
            <i class="bi bi-pencil-square text-warning me-2"></i>Edit Board
          </h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        
        <!-- Body -->
        <div class="modal-body">
          <input type="hidden" name="boardId" value="${board.id}" />
          <input type="hidden" name="workspaceId" value="${board.workspaces_id}" />

          <!-- Board Title -->
          <div class="mb-3">
            <label for="boardTitle${board.id}" class="form-label fw-semibold">Board Title</label>
            <input type="text" class="form-control bg-dark text-light border-secondary" 
                   id="boardTitle${board.id}" name="title" value="${board.title}" required />
          </div>

          <!-- Background Color Picker -->
<div class="mb-3">
  <label class="form-label fw-semibold">Board Background</label>
  <div class="d-flex flex-wrap gap-2">

    <!-- No Color / Keep Default -->
    <input type="radio" class="btn-check" 
           name="background" 
           value="" 
           id="edit-color-none-${board.id}" 
           <c:if test="${empty board.background}">checked</c:if> />
    <label class="btn rounded-circle border border-light d-flex align-items-center justify-content-center text-light"
           for="edit-color-none-${board.id}"
           style="width: 36px; height: 36px; background-color: transparent; cursor: pointer;">
        <i class="bi bi-slash-circle"></i>
    </label>

    <!-- Color Options -->
    <c:set var="colors" value="#0079bf,#d29034,#519839,#b04632,#89609e,#cd5a91,#4bbf6b,#00aecc,#838c91" />
    <c:forEach var="color" items="${fn:split(colors, ',')}">
      <input type="radio" class="btn-check" 
             name="background" 
             value="${color}" 
             id="edit-color-${board.id}-${fn:replace(color, '#', '')}"
             <c:if test="${color eq board.background}">checked</c:if> />
      <label class="btn rounded-circle border border-light"
             for="edit-color-${board.id}-${fn:replace(color, '#', '')}"
             style="width: 36px; height: 36px; background-color: ${color}; cursor: pointer;">
      </label>
    </c:forEach>
  </div>
</div>

        </div>
        
        <!-- Footer -->
        <div class="modal-footer border-secondary">
          <button type="submit" class="btn btn-warning">
            <i class="bi bi-save me-1"></i>Save Changes
          </button>
        </div>
      </div>
    </form>
  </div>
</div>

        
    </div>
</c:forEach>



    <!-- Optional: Create new board card -->
  <c:if test="${workspace.currentUserRole eq 'ADMIN' || workspace.currentUserRole eq 'OWNER'}">
    
    <a href="#" class="board-card create-board-card mt-2" data-bs-toggle="modal" data-bs-target="#createBoardModal">
        + Create New Board
    </a>
    </c:if>
</div>



            <c:if test="${empty boards}">
                <div class="text-light">No boards yet in this workspace.</div>
            </c:if>
        </div>
    </div>
    
  
    
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>


</body>

<!-- workspace member modal -->
<div class="modal fade" id="addMemberModal" tabindex="-1" aria-labelledby="addMemberModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content bg-dark text-light">
            <div class="modal-header">
                <h5 class="modal-title" id="addMemberModalLabel">Add Workspace Member</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/workspace/add-member" method="post">
                <div class="modal-body">
                    <input type="hidden" name="workspaceId" value="${workspace.id}" />

                    <div class="mb-3">
                        <label for="email" class="form-label">User Email</label>
                        <input type="email" class="form-control" name="email" required placeholder="user@example.com" />
                    </div>

                    <div class="mb-3">
                        <label for="role" class="form-label">Role</label>
                        <select class="form-select" name="role">
                            <option value="MEMBER">Member</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Add Member</button>
                </div>
            </form>
        </div>
    </div>
</div>



<!-- Create Board Modal -->
<div class="modal fade" id="createBoardModal" tabindex="-1" aria-labelledby="createBoardModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <form action="${pageContext.request.contextPath}/board/create" method="post">
      <div class="modal-content bg-dark text-light rounded-3 shadow-lg">
        
        <!-- Header -->
        <div class="modal-header border-secondary">
          <h5 class="modal-title" id="createBoardModalLabel">
            <i class="bi bi-kanban text-info me-2"></i>Create New Board
          </h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        
        <!-- Body -->
        <div class="modal-body">
          <input type="hidden" name="workspaceId" value="${workspace.id}" />

          <!-- Board Title -->
          <div class="mb-3">
            <label for="boardTitle" class="form-label fw-semibold">Board Title</label>
            <input type="text" class="form-control bg-dark text-light border-secondary" 
                   id="boardTitle" name="title" placeholder="Enter board title" required />
          </div>

          <!-- Background Color Picker -->
          <div class="mb-3">
            <label class="form-label fw-semibold">Board Background</label>
            <div class="d-flex flex-wrap gap-2">
              <c:set var="colors" value="#0079bf,#d29034,#519839,#b04632,#89609e,#cd5a91,#4bbf6b,#00aecc,#838c91" />
              <c:forEach var="color" items="${fn:split(colors, ',')}">
                <input type="radio" class="btn-check" 
                       name="background" 
                       value="${color}" 
                       id="color-${fn:replace(color, '#', '')}" 
                       />
                <label class="btn rounded-circle border border-light"
                       for="color-${fn:replace(color, '#', '')}"
                       style="width: 36px; height: 36px; background-color: ${color}; cursor: pointer;">
                </label>
              </c:forEach>
            </div>
          </div>
        </div>
        
        <!-- Footer -->
        <div class="modal-footer border-secondary">
          <button type="submit" class="btn btn-success">
            <i class="bi bi-check2-circle me-1"></i>Create
          </button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Highlight selected color -->
<style>
  /* Outline when the radio is checked */
  .btn-check:checked + label {
    outline: 3px solid #fff;
    outline-offset: 2px;
  }

  /* Optional hover effect */
  .btn-check + label:hover {
    outline: 2px solid rgba(255,255,255,0.5);
    outline-offset: 1px;
  }
</style>


<script>
  // Highlight selected color in the board creation modal
  document.addEventListener("DOMContentLoaded", () => {
    const radios = document.querySelectorAll('input[name="background"]');
    const labels = document.querySelectorAll('label[for^="color-"]');

    radios.forEach(radio => {
      radio.addEventListener('change', function () {
        // Reset outlines for all labels
        labels.forEach(label => {
          label.style.outline = "";
        });

        // Add white outline to the selected color
        const selected = document.querySelector(`label[for="${board.id}"]`);
        if (selected) {
          selected.style.outline = "3px solid #fff";
          selected.style.outlineOffset = "2px"; // optional nicer effect
        }
      });
    });
  });
</script>



<c:if test="${not empty wsmError}">
    <script>
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: '${wsmError}',
            confirmButtonColor: '#d33'
        });
    </script>
</c:if>

<c:if test="${not empty wsmSuccess}">
    <script>
        Swal.fire({
            icon: 'success',
            title: 'Success',
            text: '${wsmSuccess}',
            confirmButtonColor: '#3085d6'
        });
    </script>
</c:if>



</html>

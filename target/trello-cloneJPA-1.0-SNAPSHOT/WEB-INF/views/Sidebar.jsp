<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet" />

<style>
		.sidebar {
		    background-color: #2c2c3e;
		    min-height: 100vh;   /* 🔹 ensures it covers full screen height */
		    height: auto;        /* 🔹 allows it to grow if content exceeds viewport */
		    padding-top: 20px;
		    position:related;
		    top: 0;
    		left: 0;
		}

        .sidebar a {
            color: #bbb;
            text-decoration: none;
            display: block;
            padding: 10px 20px;
        }
        .sidebar a:hover {
            background-color: #3a3a4d;
            color: white;
        }
        .sidebar a.active {
		    background-color:darkcyan;
		    color: white;
		    border-radius: 5px;
		}
        
</style>

<div class="col-md-2 sidebar">
    <a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-home me-2"></i> Dashboard</a>

    <hr class="text-secondary">

    <strong class="px-3 d-block text-uppercase text-secondary small mb-2">Admin Workspaces</strong>
    
    <c:if test="${empty adminWorkspaces}">
	    <div class=" px-3">No admin workspaces</div>
	</c:if>
    
    
    <c:forEach var="ws" items="${adminWorkspaces}">
        <a href="${pageContext.request.contextPath}/workspace/${ws.id}"
               class="${selectedWorkspaceId == ws.id ? 'active text-white' : ''}">
            <i class="fas fa-briefcase me-2"></i> ${ws.name}
        </a>
    </c:forEach>
    
    

    <strong class="px-3 d-block text-uppercase text-secondary small mt-3 mb-2">Member Workspaces</strong>
    <c:forEach var="ws" items="${memberWorkspaces}">
        <a href="${pageContext.request.contextPath}/workspace/${ws.id}"        
        class="${selectedWorkspaceId == ws.id ? 'active text-white' : ''}">
            <i class="fas fa-users me-2"></i> ${ws.name}
        </a>
    </c:forEach>

    
</div>




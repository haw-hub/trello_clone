<style>
	.navbar {
            background-color: #2c2c3e;
            top:0;
            z-index:1000;
            position:sticky;
        }
        .navbar-brand {
            font-weight: bold;
            color: #fff !important;
        }
        .dropdown-menu {
            background-color: #2c2c3e;
        }
        .dropdown-item {
            color: white;
        }
        .dropdown-item:hover {
            background-color: #444;
        }
        .nav-item{
  		    background-color: darkcyan;
			border-radius:5px;
        }
        
</style>
<nav class="navbar navbar-expand-lg navbar-dark px-3">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-layer-group me-2"></i> TrelloClone hahaha
                </a>

                <div class="ms-auto">
                    <ul class="navbar-nav">
                        <!-- Profile Dropdown -->
                        <li class="nav-item dropdown" style="z-index:1020;">
                            <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="profileDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <span>${loggedInUser.username}'s Profile</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="profileDropdown">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/settings">Settings</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Logout">Logout</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>

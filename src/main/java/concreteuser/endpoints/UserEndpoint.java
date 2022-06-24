package concreteuser.endpoints;


import concreteuser.entity.Role;
import concreteuser.entity.User;
import concreteuser.enums.RoleEnum;
import concreteuser.gs_ws.*;
import concreteuser.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import java.util.ArrayList;
import java.util.List;

@Endpoint
public class UserEndpoint
{
		private static final String NAMESPACE_URI = "http://www.concreteuser/user-ws";

		@Autowired
		private UserService userService;

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserByLoginRequest")
		@ResponsePayload
		public GetUserByLoginResponse getUser(@RequestPayload GetUserByLoginRequest request)
		{
				GetUserByLoginResponse response = new GetUserByLoginResponse();
				UserRolesInfo userRolesInfo = new UserRolesInfo();
				User user = userService.getUserByLogin(request.getLogin());
				if(user.getRoles() != null)
						user.getRoles().stream().forEach((role) -> {
								RoleInfo roleInfo = new RoleInfo();
								roleInfo.setName(role.getName().getTitle());
								userRolesInfo.getRoles().add(roleInfo);
						});
				BeanUtils.copyProperties(user, userRolesInfo);
				response.setUserRolesInfo(userRolesInfo);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
		@ResponsePayload
		public GetAllUsersResponse getAllUsers()
		{
				GetAllUsersResponse response = new GetAllUsersResponse();
				List<UserInfo> userInfoList = new ArrayList<>();
				List<User> userList = userService.getAllUsers();
				for(int i = 0; i < userList.size(); i++)
				{
						UserInfo userInfo = new UserInfo();
						BeanUtils.copyProperties(userList.get(i), userInfo);
						userInfoList.add(userInfo);
				}
				response.getUserInfo().addAll(userInfoList);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
		@ResponsePayload
		public AddUserResponse addUser(@RequestPayload AddUserRequest request)
		{
				AddUserResponse response = new AddUserResponse();
				ServiceStatus serviceStatus = new ServiceStatus();
				User user = new User();
				user.setLogin(request.getUserRolesInfo().getLogin());
				user.setName(request.getUserRolesInfo().getName());
				user.setPassword(request.getUserRolesInfo().getPassword());
				user.setRoles(this.getRoleListFromRolesInfoList(request.getUserRolesInfo().getRoles(), user));
				if(!userService.addUser(user))
				{
						serviceStatus.setStatusCode("CONFLICT");
						serviceStatus.setMessage("Content Already Available");
				}
				else
				{
						BeanUtils.copyProperties(user, request.getUserRolesInfo());
						response.setUserRolesInfo(request.getUserRolesInfo());
						serviceStatus.setStatusCode("SUCCESS");
						serviceStatus.setMessage("Content Added Successfully");
				}
				response.setServiceStatus(serviceStatus);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
		@ResponsePayload
		public UpdateUserResponse updateUser(@RequestPayload UpdateUserRequest request)
		{
				ServiceStatus serviceStatus = new ServiceStatus();
				UpdateUserResponse response = new UpdateUserResponse();
				User user = new User();
				user.setLogin(request.getUserRolesInfo().getLogin());
				user.setName(request.getUserRolesInfo().getName());
				user.setPassword(request.getUserRolesInfo().getPassword());
				if(request.getUserRolesInfo().getRoles() != null)
						user.setRoles(this.getRoleListFromRolesInfoList(request.getUserRolesInfo().getRoles(), user));
				if(userService.updateUser(user))
				{
						UserRolesInfo userRolesInfo = new UserRolesInfo();
						if(user.getRoles() != null)
								user.getRoles().stream().forEach((role) -> {
										RoleInfo roleInfo = new RoleInfo();
										roleInfo.setName(role.getName().getTitle());
										userRolesInfo.getRoles().add(roleInfo);
								});
						BeanUtils.copyProperties(user, userRolesInfo);
						serviceStatus.setStatusCode("SUCCESS");
						serviceStatus.setMessage("Content Updated Successfully");
				}
				else
				{
						serviceStatus.setStatusCode("ERROR");
						serviceStatus.setMessage("Data entered incorrectly");
				}
				response.setServiceStatus(serviceStatus);
				return response;
		}

		@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
		@ResponsePayload
		public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request)
		{
				User user = userService.getUserByLogin(request.getLogin());
				ServiceStatus serviceStatus = new ServiceStatus();
				if(user == null)
				{
						serviceStatus.setStatusCode("FAIL");
						serviceStatus.setMessage("Content Not Available");
				}
				else
				{
						userService.deleteUser(user.getLogin());
						serviceStatus.setStatusCode("SUCCESS");
						serviceStatus.setMessage("Content Deleted Successfully");
				}
				DeleteUserResponse response = new DeleteUserResponse();
				response.setServiceStatus(serviceStatus);
				return response;
		}

		private List<Role> getRoleListFromRolesInfoList(List<RoleInfo> rolesInfoList, User user)
		{
				List<Role> roleList = new ArrayList<>();
				rolesInfoList.stream().forEach(roleInfo -> {
						Role role = new Role();
						role.setName(RoleEnum.getByString(roleInfo.getName()));
						role.setUserLogin(user.getLogin());
						roleList.add(role);
				});
				return roleList;
		}
}

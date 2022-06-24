package concreteuser.service;

import concreteuser.entity.Role;
import concreteuser.entity.User;
import concreteuser.repository.RoleRepository;
import concreteuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService
{
		@Autowired
		private UserRepository userRepository;

		@Autowired
		private RoleRepository roleRepository;

		@Override
		public List<User> getAllUsers()
		{
				List<User> list = new ArrayList<>();
				userRepository.findAll().forEach(user -> list.add(user));
				return list;
		}

		@Override
		public User getUserByLogin(String login)
		{
				User user = userRepository.findByLogin(login);
				List<Role> roleList = roleRepository.findRoleByUserLogin(login);
				if(user != null)
						user.setRoles(roleList);
				return user;
		}

		@Override
		public boolean addUser(User user)
		{
				if(!this.performUserValidation(user))
						return false;
				User foundUser = getUserByLogin(user.getLogin());
				if(foundUser != null)
						return false;
				if(user.getRoles() != null)
						user.getRoles().forEach(role -> {
								role.setUserLogin(user.getLogin());
								roleRepository.save(role);
						});
				userRepository.save(user);
				return true;
		}

		@Override
		public boolean updateUser(User user)
		{
				User foundUser = getUserByLogin(user.getLogin());
				if(foundUser != null)
				{
						if(user.getRoles() == null && foundUser.getRoles() != null)
						{
								foundUser.getRoles().stream().forEach(role -> roleRepository.delete(role));
						}
						if(user.getRoles() != null)
						{
								user.getRoles().stream().forEach(role -> role.setUserLogin(user.getLogin()));
								if(foundUser.getRoles() != null)
								{
										List<Role> roles = user.getRoles().stream().filter(role -> foundUser.getRoles().
											contains(role) || !foundUser.getRoles().contains(role)).collect(Collectors.toList());
										user.setRoles(roles);
										foundUser.getRoles().stream().forEach(role -> {
												if(!roles.contains(role))
														roleRepository.delete(role);
										});
								}
								user.getRoles().stream().forEach(role -> roleRepository.save(role));
						}
						userRepository.save(user);
						return true;
				}
				return false;
		}

		@Override
		public void deleteUser(String login)
		{
				User user = getUserByLogin(login);
				if(user != null)
				{
						userRepository.delete(user);
						if(user.getRoles() != null)
								user.getRoles().forEach(role -> roleRepository.delete(role));
				}
		}

		private boolean performUserValidation(User user)
		{
				if(user == null)
						return false;
				if(user.getLogin() == null || user.getPassword() == null || user.getName() == null)
						return false;
				if(!Character.isUpperCase(user.getPassword().charAt(0)) || !Pattern.compile("[0-9]").matcher(user.getPassword()).find())
						return false;
				return true;
		}
}
package concreteuser.service;

import concreteuser.entity.User;

import java.util.List;

public interface UserService
{
		List<User> getAllUsers();
		User getUserByLogin(String login);
		boolean addUser(User user);
		boolean updateUser(User user);
		void deleteUser(String login);
}

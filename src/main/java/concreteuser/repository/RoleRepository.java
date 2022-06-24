package concreteuser.repository;

import concreteuser.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role, Long>
{
		Role findById(long id);
    List<Role> findRoleByUserLogin (String userLogin);
}

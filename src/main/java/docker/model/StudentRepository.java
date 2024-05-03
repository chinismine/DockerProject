package docker.model;

import org.springframework.data.jpa.repository.JpaRepository;

import docker.bean.Student;


public interface StudentRepository extends JpaRepository<Student, Integer>{

}

package docker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import docker.bean.Student;
import docker.model.StudentRepository;

@RestController
public class ShowAllController {
	
	@Autowired
	StudentRepository sRepo;
	
	@PostMapping("all")
	public List<Student> showAllActivities() {
		
		return sRepo.findAll();
	}

}

package tn.rapid_post.agence;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.util.SpringReactiveModelAdditionsUtils;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.retourB3Rep;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.PermissionRepository;
import tn.rapid_post.agence.sec.repo.RoleRepository;
import tn.rapid_post.agence.sec.repo.UserRepository;
import tn.rapid_post.agence.sec.service.AppUserInterfaceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


@SpringBootApplication
public class AgenceApplication {
	@Autowired
	private retourB3Rep b3Rep;
@Autowired
 	AppUserInterfaceImpl appUserInterface;
@Autowired
private PermissionRepository permissionRepository;

    public AgenceApplication(AppUserInterfaceImpl appUserInterface) {
        this.appUserInterface = appUserInterface;
    }

    public static void main(String[] args) {
		SpringApplication.run(AgenceApplication.class, args);
	}
	@Bean
		CommandLineRunner start(){
			return args->{
//appUserInterface.AddUser(new AppUser("hechmi","hechmi"));
//appUserInterface.AddNewRole(new AppRole("ADMIN"));
//appUserInterface.AddRoleToUser("hechmi","ADMIN");
//for (AppRole appUser:appUserInterface.LoadUserByUserName("bilel").getRoles()){
//	System.out.println(appUser.getName());
//}


//RetourB3 retourB3=new RetourB3("123456","hdhhdhd");
//b3Rep.save(retourB3);
//				List<RetourB3> retourB3List=b3Rep.findAll();
//				for (RetourB3 b3:retourB3List){
//					System.out.println(b3.getId());
//				}
			};
		}

	}




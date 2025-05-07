package tn.rapid_post.agence;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.retourB3Rep;

import java.util.List;
import java.util.Scanner;


@SpringBootApplication
public class AgenceApplication {
	@Autowired
	private retourB3Rep b3Rep;

	public static void main(String[] args) {
		SpringApplication.run(AgenceApplication.class, args);
	}
	@Bean
		CommandLineRunner start(){
			return args->{

//RetourB3 retourB3=new RetourB3("123456","hdhhdhd");
//b3Rep.save(retourB3);
//				List<RetourB3> retourB3List=b3Rep.findAll();
//				for (RetourB3 b3:retourB3List){
//					System.out.println(b3.getId());
//				}
			};
		}

	}




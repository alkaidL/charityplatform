package com.seu.beyondtheboundary.charityplatform.controller;

import com.seu.beyondtheboundary.charityplatform.domain.Project;
import com.seu.beyondtheboundary.charityplatform.domain.User;
import com.seu.beyondtheboundary.charityplatform.repository.UserRepository;
import com.seu.beyondtheboundary.charityplatform.service.ProjectService;
import com.seu.beyondtheboundary.charityplatform.service.UserServiceImpl;
import com.seu.beyondtheboundary.charityplatform.util.ConstraintViolationExceptionHandler;
import com.seu.beyondtheboundary.charityplatform.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 用户主页空间控制器.
 * 
 * @since 1.0.0 2017年3月25日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {

	public static final String root = "src/main/resources/static/userimages";

	@Autowired
	private ProjectService projectService;

	@Autowired
    private UserRepository userRepository;
	//@RequestBody Project project
	//String title ,String summary, String content

	@GetMapping("/projects/edit")
	public String show() {
		return "/person/projectedit";
	}

	@GetMapping("/person/hello")
	public String ss() {
		return "/person/hello";
	}

	@PostMapping("/projects/edit")
	public ResponseEntity<Response> saveProject(String title ,String summary, String content) {
		System.out.println("before  saving project" );
		Project project = new Project(title,summary,content);
		try {
			projectService.saveProject(project);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		System.out.println(title+"||||||"+summary+"||||||"+content);
//		System.out.println(project.getSummary()+"summary!");
			System.out.println("I am saving project");
        String redirectUrl="/u/person/hello";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    @GetMapping("/complete_user_info")
    public String complete_user_info() {
        return "/person/complete_personal_information";
    }

	@GetMapping("/I_want_verify")
	public String I_want_verify() {
		return "/person/I_want_verify";
	}

	@GetMapping("/user_donate")
	public String user_donate() {
		return "/person/user_donate";
	}

    @PostMapping("/complete_user_info")
    public String complete_user_info1(User user, HttpServletRequest request, HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		//使用request对象的getSession()获取session，如果session不存在则创建一个
		HttpSession session = request.getSession();
		//将数据存储到session中
		User user1 = (User) session.getAttribute("user");
		user1.setSex(user.isSex());
		if(user.getTel() != "")
			user1.setTel(user.getTel());
		if(user.getAddress() != "")
			user1.setAddress(user.getAddress());
		if(user.getUser_id_card() != "")
			user1.setUser_id_card(user.getUser_id_card());
		if(user.getRealname() != "")
			user1.setRealname(user.getRealname());
		if(user.getEmail() != "")
			user1.setEmail(user.getEmail());

		userRepository.save(user1);
        return "redirect:/personal_center";
    }

	@GetMapping("/user_commit_verify")
	public String user_commit_verify() {
		return "/person/complete_personal_information";
	}

	@PostMapping("/user_commit_verify")
	public String user_commit_verify1(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, User user, HttpServletRequest request, HttpServletResponse response) {

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		//使用request对象的getSession()获取session，如果session不存在则创建一个
		HttpSession session = request.getSession();
		//将数据存储到session中
		User user1 = (User) session.getAttribute("user");
		user1.setSex(user.isSex());
		if(user.getTel() != "")
			user1.setTel(user.getTel());
		if(user.getAddress() != "")
			user1.setAddress(user.getAddress());
		if(user.getUser_id_card() != "")
			user1.setUser_id_card(user.getUser_id_card());
		if(user.getRealname() != "")
			user1.setRealname(user.getRealname());
		if(user.getEmail() != "")
			user1.setEmail(user.getEmail());

		user1.setVerified(2);		//设为2，让该用户进入待审核列表

		if(!file.isEmpty()) {
			try {
				Files.copy(file.getInputStream(), Paths.get(root, file.getOriginalFilename()));
				redirectAttributes.addFlashAttribute("message", "you successfully uploaded " + file.getOriginalFilename() + "!");
			} catch (IOException | RuntimeException e) {
				redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " =>" + e.getMessage());
			}
		} else {
			redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " because it was empty");
		}


		userRepository.save(user1);
		return "redirect:/personal_center";
	}


}




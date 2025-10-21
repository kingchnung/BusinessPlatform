//package com.bizmate.project.controller;
//
//import com.bizmate.project.dto.request.ProjectMemberDTO;
//import com.bizmate.project.dto.response.ProjectMemberResponseDTO;
//import com.bizmate.project.service.ProjectMemberService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/bizmate/project/member")
//@RequiredArgsConstructor
//public class ProjectMemberController {
//
//    private final ProjectMemberService projectMemberService;
//
//    @GetMapping("/list")
//    public List<ProjectMemberResponseDTO> getMembers (Long projectId){
//        return projectMemberService.list(projectId);
//    }
//
//    @GetMapping("/{projectId}/{userId}")
//    public ProjectMemberResponseDTO get (
//            @PathVariable(name = "projectId") Long projectId,
//            @PathVariable(name = "userId")Long userId){
//
//        return projectMemberService.get(projectId,  userId);
//    }
//
//    @PostMapping(value = "/")
//    public Map<String, String> register(@RequestBody ProjectMemberDTO requestDTO){
//        projectMemberService.register(requestDTO);
//        return Map.of("RESULT","SUCCESS");
//    }
//
//    @PutMapping("/{projectId}/{userId}")
//    public Map<String,String> modify (@RequestBody ProjectMemberDTO requestDTO,
//                                      @PathVariable(name = "projectId") Long projectId,
//                                      @PathVariable(name = "userId") Long userId){
//        projectMemberService.modify(requestDTO, projectId, userId);
//        return Map.of("RESULT","SUCCESS");
//    }
//
//    @DeleteMapping("/{projectId}/{userId}")
//    public Map<String, String> remove  (@PathVariable (name = "projectId") Long projectId,
//                                        @PathVariable (name = "userId") Long userId){
//        projectMemberService.remove(projectId,userId);
//        return Map.of("RESULT","SUCCESS");
//    }
//
//
//
//}

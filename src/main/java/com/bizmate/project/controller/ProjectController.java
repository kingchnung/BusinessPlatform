package com.bizmate.project.controller;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.project.dto.request.ProjectRequestDTO;
import com.bizmate.project.dto.response.ProjectResponseDTO;
import com.bizmate.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bizmate/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    // 프로젝트 리스트 데이터
    @GetMapping("/list")
    //@PreAuthorize() 나중에 관리자 및 사원 로그인 권한 추가
    public PageResponseDTO<ProjectResponseDTO> list (PageRequestDTO pageRequestDTO){
        return projectService.list(pageRequestDTO);
    }

    // 프로젝트 상세 데이터
    @GetMapping("/{id}")
    //@PreAuthorize() 나중에 관리자 및 사원 로그인 권한 추가
    public ProjectResponseDTO get(@PathVariable(name = "id") Long id){
        return projectService.get(id);
    }


    // DB 입력
    @PostMapping(value = "/")
    //@PreAuthorize() 나중에  관리자 로그인 권한 추가
    public Map<String, Long> register(@RequestBody ProjectRequestDTO requestDTO){
        Long id =  projectService.register(requestDTO);
        return Map.of("정상 id: ",id);
    }

    // 수정
    @PutMapping("/{id}")
    //@PreAuthorize(hashRole ) 나중에 관리자 로그인 권한 추가
    public Map<String,String> modify (@RequestBody ProjectRequestDTO requestDTO,
                                      @PathVariable(name = "id") Long id){
        projectService.modify(requestDTO, id);
        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> remove(@PathVariable(name = "id") Long id){
        projectService.remove(id);
        return Map.of("RESULT","SUCCESS");
    }

}

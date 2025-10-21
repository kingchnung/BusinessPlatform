package com.bizmate.project.controller;

import com.bizmate.project.dto.request.ProjectTaskDTO;
import com.bizmate.project.dto.response.TaskResponseDTO;
import com.bizmate.project.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bizmate/project/assign")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    // 멤버에 연결된 업무 불러오기

    @GetMapping("/{id}")
    //@PreAuthorize() 관리자, 사원 로그인 권한 추가
    public TaskResponseDTO get(@PathVariable(name = "id")Long id){
        return taskService.get(id);
    }

    // 업무 입력
    @PostMapping(value = "/")
    //@PreAuthorize() 관리자, 로그인 권한 추가
    public Map<String,Long> register(@RequestBody ProjectTaskDTO requestDTO){
        Long id = taskService.register(requestDTO);
        return Map.of("SUCCESS_ID:", id);
    }

    // 업무 수정
    @PutMapping("/{id}")
    //@PreAuthorize() 관리자, 로그인 권한 추가
    public Map<String,String> modify(@RequestBody ProjectTaskDTO requestDTO,
                                   @PathVariable(name = "id") Long id){
        taskService.modify(requestDTO,id);
        return Map.of("RESULT","SUCCESS");
    }

    // 업무 삭제
    @DeleteMapping("/{id}")
    //@PreAuthorize() 관리자, 로그인 권한 추가
    public Map<String , String> remove (@PathVariable(name = "id") Long id){

        taskService.remove(id);
        return  Map.of("RESULT", "SUCCESS");
    }


}

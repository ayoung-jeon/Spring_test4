package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    // 여러개의 이름으로 찾을꺼라 뒤에 In을 써줌
    // 쿼리로 표현하면?
    // select * from folder where user_id = 1 and name in ('1', '2', '3');
    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);

    List<Folder> findAllByUser(User user);
}

package com.example.instaclone_9room.domain;

import com.example.instaclone_9room.domain.baseEntity.BaseEntity;
import com.example.instaclone_9room.domain.enumPackage.Gender;
import com.example.instaclone_9room.domain.follow.Follow;
import com.example.instaclone_9room.domain.follow.Follower;
import com.example.instaclone_9room.domain.reels.Reels;
import com.example.instaclone_9room.domain.reels.ReelsComment;
import com.example.instaclone_9room.domain.reels.ReelsLikes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_entity_id")
    private Long id;

    private String username;
    private String password;
    private String role;

    private String name;
    private Boolean onPrivate=true;
    private String introduction;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthday;
    private String link;
    private String imagePath;

    private Integer followCount;
    private Integer followerCount;






    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<Follower> followers = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<Follow> follows = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<Reels> reels = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<ReelsComment> reelsComments = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<ReelsLikes> reelsLikes = new ArrayList<>();






    //--------연관관계 메서드---------------


    public void setInfo(String name,Gender gender,LocalDate birthday,String link,String introduction,Boolean onPrivate){
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.link = link;
        this.introduction = introduction;
        this.onPrivate=onPrivate;
    }



    public void addReels(Reels reels) {
        reels.setUserEntity(this);
        this.reels.add(reels);
    }

    public void addFollowCount(){
        this.followCount++;
    }
    public void minusFollowCount(){
        this.followCount--;
    }



    public void addFollowerCount(){
        this.followerCount++;
    }
    public void minusFollowerCount(){
        this.followerCount--;
    }



}

package com.example.instaclone_9room.domain.follow;

import com.example.instaclone_9room.domain.UserEntity;
import com.example.instaclone_9room.domain.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follower extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @OneToMany
    private List<CloseFollower> closeFollowers=new ArrayList<>();

    @OneToMany
    private List<BlockedFollower> blockedFollowers=new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_user_id")
    private UserEntity followerUser;
}

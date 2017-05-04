/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dell.isg.smi.virtualnetwork.entity.BaseEntity;

@Repository
public interface BaseEntityRepository extends CrudRepository<BaseEntity, Long> {

}

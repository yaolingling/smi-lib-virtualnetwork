/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The Class RepositoryConfig.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.dell.smi.virtualnetwork.repository")
public class RepositoryConfig {

}

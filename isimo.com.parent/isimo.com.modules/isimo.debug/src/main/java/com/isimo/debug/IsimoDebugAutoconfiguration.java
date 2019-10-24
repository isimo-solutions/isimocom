package com.isimo.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.isimo.core.DefaultExecutionController;
import com.isimo.core.IExecutionController;
import com.isimo.core.IsimoCoreAutoconfiguration;
import com.isimo.core.IsimoProperties;
import com.isimo.core.TestExecutionManager;

@AutoConfigureBefore(IsimoCoreAutoconfiguration.class)
@Configuration
@ComponentScan(basePackageClasses = { DebugExecutionController.class})
public class IsimoDebugAutoconfiguration {
	
	@Autowired
	DebugExecutionController debugExecutionController;
	
	@Bean
	@ConditionalOnMissingBean(name="executionController")
	IExecutionController executionController() {
		return debugExecutionController;
	}
}

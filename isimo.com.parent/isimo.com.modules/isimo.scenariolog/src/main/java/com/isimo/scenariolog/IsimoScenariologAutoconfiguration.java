package com.isimo.scenariolog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.isimo.core.IsimoCoreAutoconfiguration;

@AutoConfigureBefore(IsimoCoreAutoconfiguration.class)
@Configuration
@ComponentScan(basePackageClasses = { ScenarioLogExecutionListener.class})
public class IsimoScenariologAutoconfiguration {
	
}

package com.retail.poc.bdd.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:cucumber/features", glue = { "com.retail.poc.bdd" }, format = {
		"json:target/cucumber/cucumber-report.json",
		"html:target/cucumber/plain-html-reports" }, tags = { "~@Ignore" })
public class RunCucumberIntegrationTest {

}

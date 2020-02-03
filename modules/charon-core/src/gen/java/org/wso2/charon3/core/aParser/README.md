#### Steps to generate PATH parser using parse2. 

1. Using the ABNF metalanguage, define the rules for "path" attribute as described in the SCIM2 specification. You 
can find the already defined rules for path attribute in path-abnf-rules.abnf file. 
2. Download the parse2 jar from the [website](http://www.parse2.com/), here we used aparse-2.5.jar.
3. Run the following command to generate the PATH parser source code.
 ```java -cp aparse-2.5.jar com.parse2.aparse.Parser scim-filter-rules.adnf```
4. Format the code and remove the Displayer.java, XmlDisplayer.java files since we don't need those. 


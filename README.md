natankuj - custom fuel prices database with prices parsed from <a href="http://auto.sme.sk/natankuj/bratislavsky-kraj">natankuj.sme.sk</a>  
implemented as school project - first usage of relational database later also with usage of ORM and WildFly application server  
  
currently split into 3 branches:
<ol>
  <li>master - first iteration - focused on parsing + ORM (Hibernate). NetBeans project!</li>
  <li>elastic - second iteration - experimenting with ElasticSearch NoSQL database in order to provide users with autocomplete text fields. NetBeans project!</li>
  <li>j2e - third iteration - basically remastered and tweaked first iteration. Application in this version is running on the application server (WildFly 10) and is divided into separate client/server/API parts. Added features like PDF export, separate threads for data parsing, logging (log4j) and multilanguage support. Eclipse EE project!</li>
</ol>
  
Screenshots from j2e branch:  
![project_main_screen](https://drive.google.com/uc?id=0B9F-9_QAlgdGQmd1WVp5S0owemM)
![project_main_screen](https://drive.google.com/uc?id=0B9F-9_QAlgdGWGx4Q3JFTDV4QXM)  


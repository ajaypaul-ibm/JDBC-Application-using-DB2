Before running this application you need to install DB2 on your system. In case of mac os Follow the below steps :

Rancher Desktop should be installed as first step

For installing DB2:
1.Run the following command to pull the DB2 image using the x86_64 architecture (compatible with DB2):
docker pull --platform linux/amd64 ibmcom/db2


2.Create and Run the DB2 Container: Now, create and run the DB2 container by specifying the platform once again.
docker run -d --name db2_container \
 --platform linux/amd64 \
 --privileged=true \
 -p 50000:50000 \
 -e LICENSE=accept \
 -e DB2INST1_PASSWORD=your_password \
 -e DBNAME=testdb \
 -v /db2_data:/database \
 ibmcom/db2


3.Verify the Container: After running the container, you can verify that it's running properly with:
docker ps

4. docker start dockerid(which you will get when docker is created)
5.Connect to DB2: Once the container is running, connect to the DB2 instance as described previously:
docker exec -it db2_container bash -c "su - db2inst1"
db2 connect to testdb 


6.source /database/config/db2inst1/sqllib/db2profile 

7.db2start 

Now for running the project go to terminal and cd into the project folder
Then compile like below :


 javac -cp .:db2jcc.jar *.java    

Now we can run the specific test class , for eg if we want to run ListDemographicInfo then execute command like this :


java -cp .:db2jcc.jar ListDemographicInfo   

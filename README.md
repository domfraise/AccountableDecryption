# AccountableDecryption
A system for making decryption accountable

Contains 4 main parts - 
A java  servlet webserver - Hosts a static web site - recieves decrpytion requests and adds to a merkel tree stored in a database.

The database - contains a merkle tree to store hash's of files that have been requested, and hash of all information in a decrpytion request

sgx device - An emulation of a secure sgx device that stores the keys and performs decrpytion - see https://github.com/sewelol/sgx-decryption-service for details on the device. Webserver contacts the sgx device via gRPC. See DeviceRPC/DeviceClient.java for usable method calls. Original gRPC files can be found in io.grpc.decrpytiondevice

The website - 2 types of users - admin/decrpytion requesters and users/inspectors. admin users can search for files stored in the database by time and date and make a decyption request, then download the file decrypted file. An inspector can then inspect the log stored in the database to see if any of their files their files have been decrypted. 

<h2>How To Use</h2>
Run the web server - run JavaServer/JavaServer.java - servelt funtionality in JavaServer/Servlet.java
NB - Database is stored on University of Birmingham server so you will need to be on the UoB VPN for the intial connetction to wor. Alternitavly change Database/Database.java to your own postgres databse - port 8080 localhost

Run the SGX Go server - see https://github.com/sewelol/sgx-decryption-service for up to date files and running instructions. Requires golang to be installed - port 50051 localhost

Use the website to request decryptions and generate cryptigraphic proofs.

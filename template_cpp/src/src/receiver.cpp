#include "receiver.h"

#include <iostream>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <stdio.h>
#include <string.h>	
#include <unistd.h>

using namespace std;

Receiver::Receiver(in_addr_t ip, unsigned short port)
    : receiver_ip(ip), receiver_port(port)
    {
        // Create socket
        socketDescriptor = socket(AF_INET, SOCK_DGRAM, 0);

        if (socketDescriptor < 0) {
	        perror("cannot create socket");
            exit(0);
        }

        // Bind socket
        receiver_addr.sin_family = AF_INET;
        receiver_addr.sin_addr.s_addr = receiver_ip;
        receiver_addr.sin_port = receiver_port;
        memset(receiver_addr.sin_zero, '\0', sizeof(receiver_addr.sin_zero));

        bind(socketDescriptor, reinterpret_cast<struct sockaddr*>(&receiver_addr), sizeof(receiver_addr));
    }

Receiver::~Receiver(){
    close(socketDescriptor);
}   

ssize_t Receiver::receive(){

    struct sockaddr_in foreign_addr;
    socklen_t foreign_addr_len;
    unsigned char msg[512];

    ssize_t res_rec = recvfrom(socketDescriptor, &msg, 512, 0, reinterpret_cast<struct sockaddr*>(&foreign_addr), &foreign_addr_len);

    if (res_rec < 0){
            cerr << "Error receiving message : " << msg;
        }
        
        return res_rec;

}
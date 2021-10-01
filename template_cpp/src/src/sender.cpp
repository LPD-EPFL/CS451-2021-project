#include "sender.h"

#include <iostream>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <stdio.h>
#include <string.h>	
#include <unistd.h>

using namespace std;


Sender::Sender(in_addr_t ip, unsigned short port)
    : sender_ip(ip), sender_port(port)
    {
        
        // Create socket
        socketDescriptor = socket(AF_INET, SOCK_DGRAM, 0);

        if (socketDescriptor < 0) {
	        perror("cannot create socket");
            exit(0);
        }
    }

Sender::~Sender(){
    close(socketDescriptor);
}

ssize_t Sender::send(const char &msg, in_addr_t to_ip, unsigned short to_port){
    
    struct sockaddr_in to_addr;

    to_addr.sin_family = AF_INET;
    to_addr.sin_port = to_port;
    to_addr.sin_addr.s_addr = to_ip;
    memset(to_addr.sin_zero, '\0', sizeof(to_addr.sin_zero));


    ssize_t res_msg = sendto(socketDescriptor, &msg, strlen(&msg), 0,  reinterpret_cast<struct sockaddr*>(&to_addr), sizeof(to_addr));

    if (res_msg < 0){
        cerr << "Error sending message : " << msg;
    }
    
    return res_msg;

}


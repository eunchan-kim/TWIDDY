#include <SoftwareSerial.h>

#define rxPin 2 
#define txPin 3 
#define BUFF_SIZE 256 

SoftwareSerial swSerial(txPin, rxPin);

uint8_t buffer[BUFF_SIZE];
uint8_t index = 0;
uint8_t data;

void setup() {
  swSerial.begin(115200);
  Serial.begin(9600);
}

void loop() {
  // 데이터가 들어올때 까지 대기.
  while(!swSerial.available());
  
  while(swSerial.available()) {    
    data = swSerial.read();
    Serial.write(data);
    buffer[index++] = data;
    if(index == BUFF_SIZE || data == '\0') 
      break;
    delay(5);
  }   
  // 블루투스를 통하여 받은 데이터를 되돌려준다.
  for(uint8_t i = 0; i < index; ++i) {
    swSerial.write(buffer[i]);
  }
  index = 0;
}

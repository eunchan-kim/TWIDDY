#include <SoftwareSerial.h>
#include <Servo.h>

#define rxPin 2 
#define txPin 3 
#define leftArmPin 5
#define rightArmPin 6
#define ANGRY 'a'
#define HAPPY 'h'
#define NORMAL 'n'
#define START 's'
#define EXPLAIN 'e'

Servo leftArm;
Servo rightArm;

SoftwareSerial swSerial(txPin, rxPin);

char data;
int angle;

void setup() {
  swSerial.begin(115200);
  Serial.begin(9600);
  leftArm.attach(leftArmPin);
  rightArm.attach(rightArmPin);

  leftArm.write(90);
  rightArm.write(90);
}

void loop() {
  // 데이터가 들어올때 까지 대기.
  while(!swSerial.available());
  
  if(swSerial.available()) {    
    data = swSerial.read();
    Serial.write(data);
    // Angle 범위: 20 ~ 200 (180도)
    switch(data)
    {
      case ANGRY-1: 
      case ANGRY: 
      case ANGRY+1:
        moveServo(90, 120, 15);
        moveServo(120, 60, 3);
        moveServo(60, 120, 15);
        moveServo(120, 60, 3);
        moveServo(60, 120, 15);
        moveServo(120, 60, 3);
        moveServo(60, 120, 15);
        moveServo(120, 90, 3);
        break;
      case HAPPY-1:
      case HAPPY:
      case HAPPY+1:
        moveServoRev(90, 150, 3);
        moveServoRev(150, 40, 3);
        moveServoRev(40, 150, 3);
        moveServoRev(150, 40, 3);
        moveServoRev(40, 150, 3);
        moveServoRev(150, 40, 3);
        moveServoRev(40, 90, 3);
        break;
      case NORMAL-1:
      case NORMAL:
      case NORMAL+1:
        moveServo(90, 150, 15);
        delay(500);
        moveServo(150, 90, 15);
        break;
      case START-1:
      case START:
      case START+1:
        moveServoRev(90, 30, 5);
        moveServoR(150, 60, 5);
        moveServoR(60, 150, 5);
        moveServoR(150, 60, 5);
        moveServoR(60, 150, 5);
        moveServoRev(30, 90, 15);
        break;
      case EXPLAIN-1:
      case EXPLAIN:
      case EXPLAIN+1:
        moveServoL(90, 60, 10);
        moveServoL(60, 120, 10);
        moveServoL(120, 60, 10);
        moveServoL(60, 120, 10);
        moveServoL(120, 60, 10);
        moveServoL(60, 120, 10);
        moveServoL(120, 60, 10);
        moveServoL(60, 90, 10);
        break;
      default:
        break;
    }
    
    // 블루투스를 통하여 받은 데이터를 되돌려준다.
    swSerial.write(data);    
  }  
}

void moveServo(int s, int e, int t)
{ 
  if(s < e)
  {
      for(angle = s; angle < e; angle ++)
      {
        leftArm.write(angle);
        rightArm.write(180-angle);
        delay(t);
      }  
  }
  else
  {
      for(angle = s; angle > e; angle --)
      {
        leftArm.write(angle);
        rightArm.write(180-angle);
        delay(t);
      }  
  }
}

void moveServoRev(int s, int e, int t)
{ 
  if(s < e)
  {
      for(angle = s; angle < e; angle ++)
      {
        leftArm.write(angle);
        rightArm.write(angle);
        delay(t);
      }  
  }
  else
  {
      for(angle = s; angle > e; angle --)
      {
        leftArm.write(angle);
        rightArm.write(angle);
        delay(t);
      }  
  }
}

void moveServoR(int s, int e, int t)
{ 
  if(s < e)
  {
      for(angle = s; angle < e; angle ++)
      {
        rightArm.write(180-angle);
        delay(t);
      }  
  }
  else
  {
      for(angle = s; angle > e; angle --)
      {
        rightArm.write(180-angle);
        delay(t);
      }  
  }
}

void moveServoL(int s, int e, int t)
{ 
  if(s < e)
  {
      for(angle = s; angle < e; angle ++)
      {
        leftArm.write(angle);
        delay(t);
      }  
  }
  else
  {
      for(angle = s; angle > e; angle --)
      {
        leftArm.write(angle);
        delay(t);
      }  
  }
}

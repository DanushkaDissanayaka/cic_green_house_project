#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>

RF24 radio(7,8);
const int DATAIN = 3;
const byte address[6] = "00001";

void setup() {
  pinMode(DATAIN,INPUT);
  radio.begin();
  radio.openWritingPipe(address);
  radio.setPALevel(RF24_PA_MAX);
  radio.setDataRate(RF24_250KBPS);
  radio.stopListening();
}

void loop() {
  if(digitalRead(DATAIN)){
  const char text[] = "power";
  radio.write(&text, sizeof(text));
  delay(1000);  
  }
  else{
  const char text[] = "nopower";
  radio.write(&text, sizeof(text));
  delay(1000);
  }
}

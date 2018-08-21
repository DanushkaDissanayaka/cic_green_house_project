#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>

RF24 radio(7,8);

const int LED = 2;
const byte address[6] = "00001";

void setup() {
  // put your setup code here, to run once:
  pinMode(LED, OUTPUT);
  radio.begin();
  radio.openReadingPipe(0,address);
  radio.setPALevel(RF24_PA_MAX);
  radio.setDataRate(RF24_250KBPS);
  radio.startListening();
  Serial.begin(9600);
}

/*void loop() {
  char text[32] = "";
  if (radio.available()) {
    radio.read(&text, sizeof(text));
    String transData = String(text);
    if (transData == "power") {     
       Serial.println("OK");
      //delay(500);              // wait for a .5 second
      }
     if (transData == "nopower") {     
     Serial.println("NO");
     //delay(500);              // wait for a .5 second
      }      
  }
}*/


void loop() {
  char text[32] = "";
  if (radio.available()) {
    radio.read(&text, sizeof(text));
    String transData = String(text);
    Serial.println(transData);
      }      
  }

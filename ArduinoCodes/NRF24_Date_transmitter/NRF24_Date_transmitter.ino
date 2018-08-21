#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>
#include <Wire.h>
#include "ds3231.h"
#include "rtc_ds3231.h"

#define BUFF_MAX 128
uint8_t time[8];
char recv[BUFF_MAX];
unsigned int recv_size = 0;
unsigned long prev, interval = 1000;

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

  Wire.begin();
    DS3231_init(DS3231_INTCN);
    memset(recv, 0, BUFF_MAX);
    Serial.println("GET time");

}

/*void loop() {
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
}*/



void loop()
{
    char in;
    char buff[BUFF_MAX];
    unsigned long now = millis();
    struct ts t;

    // show time once in a while
    if (now - prev > interval) {
            DS3231_get(&t);
    
            // there is a compile time option in the library to include unixtime support
    #ifdef CONFIG_UNIXTIME
            snprintf(buff, BUFF_MAX, "%d.%02d.%02d %02d:%02d:%02d %ld", t.year,
                 t.mon, t.mday, t.hour, t.min, t.sec, t.unixtime);
    #else
            snprintf(buff, BUFF_MAX, "%d.%02d.%02d %02d:%02d:%02d", t.year,
                 t.mon, t.mday, t.hour, t.min, t.sec);
    #endif

        //Serial.println(buff);
        radio.write(&buff, sizeof(buff));
        prev = now;
    }
}

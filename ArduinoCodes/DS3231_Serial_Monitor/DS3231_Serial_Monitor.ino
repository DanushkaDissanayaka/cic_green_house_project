
#include <Wire.h>
#include "ds3231.h"
#include "rtc_ds3231.h"

#define BUFF_MAX 128

uint8_t time[8];
char recv[BUFF_MAX];
unsigned int recv_size = 0;
unsigned long prev, interval = 1000;

void setup()
{
    Serial.begin(9600);
    Wire.begin();
    DS3231_init(DS3231_INTCN);
    memset(recv, 0, BUFF_MAX);
    Serial.println("GET time");S

    //Serial.println("Setting time");
    //parse_cmd("T001015609062018",16);
  //           TssmmhhWDDMMYYYY
    
}

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

        Serial.println(buff);
        prev = now;
    }
}



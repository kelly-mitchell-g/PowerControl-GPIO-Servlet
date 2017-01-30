
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mattafire
 */
public class GPIOHandler {

    //constructor
    public GPIOHandler() {
        
    }
    
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        //lightPin uses gpio_16 pin and starts High to change controller off
        final GpioPinDigitalOutput lightPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16, "MyLED", PinState.HIGH);
        //outletPin uses gpio_15 pin and starts High to change controller on
        final GpioPinDigitalOutput outletPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15, "MyLED", PinState.LOW);
        //currently working on
        //lightSwitch will listen on gpio_08 for change of state to control light
        final GpioPinDigitalInput lightSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_08, "lightSwitch");
        
        //used to change pinstate to on
        public void lightOn(){
            lightPin.low();
        }
         //used to change pinstate to off
        public void lightOff(){
            lightPin.high();
        }
         //used to change pinstate to off
        public void outletOff(){
            outletPin.low();
        }
         //used to change pinstate to on
        public void outletOn(){
            outletPin.high();
        }
        //currently working on
        //used to listen for state change of light switch -- current drawback servlets dont listen for changes
        String lightSwitchCheck() throws InterruptedException{
            while(true){
            if(lightSwitch.isLow()){
                return "low";
            }
            if(lightSwitch.isHigh()){
                return "high";
            }
            wait(500);
        }
        }
}

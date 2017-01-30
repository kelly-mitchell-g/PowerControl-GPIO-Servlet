/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mattafire
 */
@WebServlet(urlPatterns = {"/Control"})
public class Control extends HttpServlet {
    //to keep track of current state of control systems
    boolean[] status = {false, false}; //Lights,Outlets
    //to keep track of last change of each system
    Date[] lastChange = {new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())}; //lights, outlets
    //to keep track of the ip of where the change command was issued from
    String[] changeFrom = new String[2]; //lights, ooutlets
    //used for converting the time to a readable formate
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    //creates gpio handler opbject for gpoi controller
    GPIOHandler gpio = new GPIOHandler();
    //used so that the program wont push null values to display
    boolean firstRun = true;//currently not used
    
    //currently working on
    //used to check lightswitch change -- drawback serverlts dont listen for state change
    String lStateCheck() throws InterruptedException{
        String state;
        while(true){
            state = gpio.lightSwitchCheck();
            if(state=="low"&&status[0]){
                lStateChange("LightSwitch", "off");
            }
            if(state=="high"&&!status[0]){
                lStateChange("LightSwitch", "on");
            }
        }
    }
    //used to change the light state
    public void lStateChange(String changedFrom, String changeState) {
        lSetStatus(changeState);
        lSetLastChange(System.currentTimeMillis());
        setChangeFrom(0, changedFrom);
    }
    //returns the last changed time and formats for the requested contoroller
    public String getLastChange(int index) {
        return sdf.format(this.lastChange[index]);
    }
    //get new changed time
    public void lSetLastChange(long milli) {
        this.lastChange[0] = new Date(milli);
    }
    //sets the new changed time for outlet
    public void oSetLastChange(long milli) {
        this.lastChange[1] = new Date(milli);
    }
    //get the statuse of a requested controller
    public String getStatus(int index) {
        if (status[index]) {
            return "On";
        } else {
            return "Off";
        }
    }
    //used to set the state of the light
    public void lSetStatus(String status) {
        if (status != null) {
            if (status.equals("Off")) {
                this.status[0] = false;
                gpio.lightOff();
            } else {
                this.status[0] = true;
                gpio.lightOn();
            }
        }
    }
    //changes the outlet statuse
    public void oSetStatus(String status) {
        if (status != null) {
            if (status.equals("Off")) {
                this.status[1] = false;
                gpio.outletOff();
            } else {
                this.status[1] = true;
                gpio.outletOn();
            }
        }
    }
    //toggle light status
    public void lToggleStatus() {
        if (status[0]) {
            lSetStatus("Off");
        } else {
            lSetStatus("On");
        }
    }
    //used to toggle outlet status
    public void oToggleStatus() {
        if (status[1]) {
            oSetStatus("Off");
        } else {
            oSetStatus("On");
        }
    }
    //changes the changed from ip to new changed from
    public void setChangeFrom(int index, String name) {
        changeFrom[index] = name;
    }
    //returns the last changed from for selected group
    public String getChangeFrom(int index) {
        return changeFrom[index];
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InterruptedException {
        //used to handle any state change for light controller
        if (request.getParameter("lChangeState") != null) {
            String changeState = request.getParameter("lChangeState");
            if (changeState != getStatus(0) && changeState != null) {
                lStateChange(request.getRemoteHost(),changeState);
                //lSetStatus(changeState);
                //lSetLastChange(System.currentTimeMillis());
                //setChangeFrom(0, request.getRemoteHost());
            }
        } 
        //used to handle any state change for outlet controller
        else if (request.getParameter("oChangeState") != null) {
            String changeState = request.getParameter("oChangeState");
            if (changeState != getStatus(1) && changeState != null) {
                oSetStatus(changeState);
                oSetLastChange(System.currentTimeMillis());
                setChangeFrom(1, request.getRemoteHost());

            }
        }
        //collects all the information needed to send it to the jsp page
        request.setAttribute("lState", getStatus(0));
        request.setAttribute("lLastChange", getLastChange(0));
        request.setAttribute("lChangeFrom", getChangeFrom(0));
        request.setAttribute("oState", getStatus(1));
        request.setAttribute("oLastChange", getLastChange(1));
        request.setAttribute("oChangeFrom", getChangeFrom(1));
        request.getRequestDispatcher("Status.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (InterruptedException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (InterruptedException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

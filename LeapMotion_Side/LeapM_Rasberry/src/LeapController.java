
import com.leapmotion.leap.*;
import com.leapmotion.leap.Finger.Type;
import com.leapmotion.leap.Gesture.State;

import java.net.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LeapController extends Listener{
	
	private static int x_pos, y_pos, z_pos, gripper;
	
	public void onConnect(Controller controller) {
		System.out.println("Connected");
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		Frame frame = controller.frame();
		HandList hands = frame.hands();
		Hand firstHand = hands.get(0);
		x_pos = (int)Math.round(firstHand.palmPosition().getX());
		y_pos = (int)Math.round(firstHand.palmPosition().getY());
		z_pos = (int)Math.round(firstHand.palmPosition().getZ());
		gripper = 0;
		//System.out.println("XInit: "+x_pos + "  " + "YInit: "+y_pos + "ZInit: "+z_pos);
	}
	
	@SuppressWarnings("unchecked")
	public void wrt_f(String x, String y, String z,String g) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("X", x);
			obj.put("Y", y);
	 		obj.put("Z", z);
	 		obj.put("G", g);
		}catch(Exception e) {}
		
 
		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter("C:\\Apache24\\htdocs\\Coordinates.json")) {
			file.append(obj.toJSONString());
			//System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("JSON Object: " + obj);
		}catch(Exception e) {}
		
	}
	
	Vector leapToWorld(Vector leapPoint, InteractionBox iBox)
	{
	    leapPoint.setZ((float)(leapPoint.getZ() * -1.0)); //right-hand to left-hand rule
	    Vector normalized = iBox.normalizePoint(leapPoint, false);
	    normalized = normalized.plus(new Vector((float)0.5, (float)0, (float)0.5)); //recenter origin
	    return normalized.times(100); //scale
	}
	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		InteractionBox ibox = frame.interactionBox(); 
		/*for(Hand h : frame.hands()) {
			//String h_type = h.isLeft() ? "Left":"Right";
			//System.out.println(h_type + "  " + ", Palm Position X: " + h.palmPosition().getX());
			Vector norm_palm = leapToWorld(h.palmPosition(),ibox);
			float x_new = (norm_palm.getY());
			float y_new = (norm_palm.getX()) ;
			float z_new = (norm_palm.getZ()) ;
			System.out.println(" Center: " + ibox.center() + ", Palm Position X: " + x_new);
			wrt_f(String.valueOf(x_new),String.valueOf(y_new), String.valueOf(z_new));
			//x_pos = x_new;
			//y_pos = y_new;
			//z_pos = z_new;
			try{
				Thread.sleep(500);
			}catch(Exception e){System.out.println("No Hands");}
		}*/
		for(Finger f : frame.fingers()) {
			if(!f.isExtended()) {
				//Vector fingerPos = f.stabilizedTipPosition();
				System.out.println("True");
				gripper = 1;
				//wrt_f("0","0","0","1");
			}
			else {
				gripper = 0;
				//wrt_f("0","0","0","0");
			}
			/*if(f.type() == Type.TYPE_THUMB && !f.isExtended()) {
				//click();
				Vector fingerPos = f.stabilizedTipPosition();
				System.out.println(fingerPos.toString());
				try {Thread.sleep(500);}catch(Exception e) {}
			}*/
		}
				
		for(Hand h : frame.hands()) {
			String h_type = h.isLeft() ? "Left":"Right";
			Vector sta_Palm= h.stabilizedPalmPosition(); 
			Vector boxPos=sta_Palm;
			//Vector boxPos=ibox.normalizePoint(sta_Palm);
			int x_new = (z_pos-(int)Math.round(boxPos.getZ()))/3;
			int y_new = (x_pos-(int)Math.round(boxPos.getX()))/4;
			int z_new = -((int)Math.round(boxPos.getY()) - y_pos)/2;
			if(x_new < 5 && x_new > -5 ) {
				x_new=0;
			}
			if(y_new < 5 && y_new > -5 ) {
				y_new=0;
			}
			if(z_new < 5 && z_new > -5 ) {
				z_new=0;
			}
			//System.out.println("X: " + boxPos.getX() + ", Y: " + boxPos.getY()+ ", Z: " + boxPos.getZ());
			wrt_f(String.valueOf(x_new),String.valueOf(y_new), String.valueOf(z_new), String.valueOf(gripper));
			x_pos = (int)Math.round(boxPos.getX());
			y_pos = (int)Math.round(boxPos.getY());
			z_pos = (int)Math.round(boxPos.getZ());
			/*try{
				Thread.sleep(2000);
			}catch(Exception e){System.out.println("No Hands");}*/
		}
		try {Thread.sleep(5000);}catch(Exception e) {}
	}

	public static void main(String[] args) {
		LeapController leap= new LeapController();
		
		Controller controller = new Controller();
		controller.addListener(leap);
		try {
			System.in.read();
		}catch(Exception e) {}
		
		controller.removeListener(leap);
		
		
		// TODO Auto-generated method stub

	}

}

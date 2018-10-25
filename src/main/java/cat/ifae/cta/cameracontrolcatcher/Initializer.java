package cat.ifae.cta.cameracontrolcatcher;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import cat.ifae.cta.cameracontrol.client.OPCUACameraControlClient;
import cat.ifae.cta.cameracontrol.client.interfaces.ClusterControl;
import cat.ifae.cta.cameracontrol.client.interfaces.ECCControl;
import cat.ifae.cta.opcua.dataaccess.interfaces.Variable;
import cat.ifae.cta.opcua.dataaccess.uaobjects.OPCUAVariable.DataInformation;

public class Initializer {
	final private OPCUACameraControlClient camera_control_client;
	final private String server; 
	static private final String _INIT_STR = "<?xml version=\"1.0\"?><configuration><URI>opc.tcp://%s:52520/OPCUAServer/CameraControl</URI></configuration>";  
	
	public Initializer() {
		this.camera_control_client = new OPCUACameraControlClient("OPCUACatcher", "OPCUACatcher program");
		//this.server = "localhost";
		this.server = "172.16.17.137";
	}
	
	public Initializer(String server) {
		this.camera_control_client = new OPCUACameraControlClient("OPCUACatcher", "OPCUACatcher program");
		this.server = server;
	}
	
	private void connect() {
		try {
			this.camera_control_client.initialize(String.format(Initializer._INIT_STR, server));
			this.camera_control_client.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void disconnect() {
		try {
			this.camera_control_client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void subscribe() {
//        ECCControl ecc_control = this.camera_control_client.getECCControl();
//        if(ecc_control.isReal()) {
//            for (String variable_name : ecc_control.getVariablesAvailable()) {
//                System.out.println(String.format("Registering: %s", variable_name));
//                Variable variable = ecc_control.getVariable(variable_name);
//                variable.addObserver(new Observer() {
//                    @Override
//                    public void update(Observable o, Object updated_object) {
//                        DataInformation variable_info = (DataInformation) updated_object;
//                        System.out.println(String.format("E - This is the status: %s", variable_info.getStatus()));
//                        System.out.println(String.format("E - This is the value: %s", variable_info.getValue()));
//                        System.out.println(String.format("E - This is the origin time: %s", variable_info.getSourceTime()));
//                    }
//                });
//            }
//        }
		ClusterControl cluster_control = this.camera_control_client.getClusterControl();
		if(cluster_control.isReal()) {
			for(String variable_name: cluster_control.getClusterVariablesAvailable()) {
				System.out.println(String.format("Registering: %s", variable_name));
				Variable variable = cluster_control.getClusterVariable(variable_name);
				variable.addObserver(new Observer() {
					
					@Override
					public void update(Observable o, Object updated_object) {
						DataInformation variable_info = (DataInformation) updated_object;
						System.out.println(String.format("C - This is the status: %s", variable_info.getStatus()));
						System.out.println(String.format("C - This is the value: %s", variable_info.getValue()));
						System.out.println(String.format("C - This is the origin time: %s", variable_info.getSourceTime()));
					}
				});
			}
			for(String variable_name: cluster_control.getPixelVariablesAvailable()) {
				System.out.println(String.format("Registering: %s", variable_name));
				Variable variable = cluster_control.getPixelVariable(variable_name);
				variable.addObserver(new Observer() {
					
					@Override
					public void update(Observable o, Object updated_object) {
						DataInformation variable_info = (DataInformation) updated_object;
						System.out.println(String.format("P - This is the status: %s", variable_info.getStatus()));
						System.out.println(String.format("P - This is the value: %s", variable_info.getValue()));
						System.out.println(String.format("P - This is the origin time: %s", variable_info.getSourceTime()));
					}
				});
			}
		}
	}
	
	public static void main (String [] args) {
		Initializer caco_catcher;
		if(args.length > 2)  caco_catcher = new Initializer(args[1]);
		else caco_catcher = new Initializer();
		caco_catcher.connect();
		caco_catcher.subscribe();
		Scanner userInput = new Scanner(System.in);
		String userSelection = userInput.next(); 
		caco_catcher.disconnect();
    }	
}

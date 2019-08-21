package aName.components.Crash cam app Functionality;

import aName.contracts.interfaces.Hybrid Encryption;
import aName.contracts.datatypes.Data;
import aName.contracts.interfaces.Uploading data;
import aName.contracts.interfaces.Authenticating crash cam operator;
import aName.contracts.datatypes.Crash cam operators' user accounts;
import aName.contracts.interfaces.Storing data;
import aName.contracts.interfaces.Access Control;
import aName.contracts.interfaces.Cryptographic Hash Function;
import aName.contracts.interfaces.Recording data;
import aName.contracts.datatypes.Anonymized data;
		
public class Crash cam app Functionality implements Storing data, Uploading data, Recording data, Authenticating crash cam operator {
	
	private Hybrid Encryption hybrid Encryption;
	private Access Control access Control;
	private Cryptographic Hash Function cryptographic Hash Function;
	
	public Crash cam app Functionality(Hybrid Encryption hybrid Encryption, Access Control access Control, Cryptographic Hash Function cryptographic Hash Function) {
		// TODO: implement and verify auto-generated constructor.
	    this.hybrid Encryption = hybrid Encryption;
	    this.access Control = access Control;
	    this.cryptographic Hash Function = cryptographic Hash Function;
	}
	
	public void storing data(Data data, Anonymized data anonymized data){
		// TODO: implement and verify auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
	public void uploading data(Data data){
		// TODO: implement and verify auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
	public void recording data(Data data){
		// TODO: implement and verify auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
	public void authenticating crash cam operator(Crash cam operators' user accounts crash cam operators' user accounts){
		// TODO: implement and verify auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}
	
}
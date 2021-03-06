import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankMessage implements Msg {
	Tanks tank;
	TankClient tc;
	int msgType = Msg.TANK_NEW_MSG;
	public TankMessage(Tanks tank){
		this.tank = tank;
	}
	public TankMessage(TankClient tc){
		this.tc = tc;
	}
	public void send(DatagramSocket ds,String IP, int udp_Port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(tank.id);
			dos.writeInt(tank.x);
			dos.writeInt(tank.y);
			dos.writeInt(tank.dir.ordinal());
			dos.writeBoolean(tank.good);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length,new InetSocketAddress(IP,udp_Port));
			ds.send(dp);
		}catch(SocketException e){
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void parse(DataInputStream dis){
		try {
			int id = dis.readInt();
			if(tc.myTank.id == id){
				return;
			}
			

			
			int x = dis.readInt();
			int y = dis.readInt();
			Dir dir = Dir.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			
			boolean exist = false;
			for(int i=0;i<tc.tanks.size();i++){
				Tanks t = tc.tanks.get(i);
				if(t.id == id){
					exist = true;
					break;
				}
			}
			if(!exist){

				TankMessage tm = new TankMessage(tc.myTank);
				tc.nc.send(tm);
				
				Tanks t = new Tanks(x,y,good,dir,tc);
				t.id = id;
				tc.tanks.add(t);
			}
			
		
//System.out.println("id:"+id+"--"+"x:"+x+"--"+"y:"+y+"--"+"dir:"+dir+"--"+"good:"+good);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

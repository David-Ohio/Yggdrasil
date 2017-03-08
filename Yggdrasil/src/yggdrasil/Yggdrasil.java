/*
 * Contact: ohio@ohiotech.com.br
 * This is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */
package yggdrasil;

import br.com.ohiotech.ohiotechlogger.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import yggdrasil.nodes.Nodes;

/**
 *
 * @author David Ohio
 */
public class Yggdrasil {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Log.info("Initializing Yggdrasil");
            InetAddress localhost = InetAddress.getLocalHost();
            List<Nodes> nodes = new ArrayList<>();
            List<String> reachable = new ArrayList<>(255);
            byte[] ip = localhost.getAddress();
            int timeout = 1000;
            for(int i=1; i<=254; i++){
                ip[3] = (byte) i;
                InetAddress address = InetAddress.getByAddress(ip);
                Log.info("Probing for address "+address);
                if(address.isReachable(timeout)){
                    Log.info(">>>"+address+" machine is turned on and can be pinged");
                    reachable.add(address.getHostAddress());
                }else if(!address.getHostAddress().equals(address.getHostName())){
                    Log.info(">>>"+address+" is known in a DNS lookup");
                }else{
                    Log.info(address+" the host address and host name are equal, meaning the host name could not be resolved.");
                }
            }
            Nodes locahost = new Nodes("127.0.0.1");
            nodes.add(locahost);
            reachable.stream().forEach((machine) -> {
                Nodes node = new Nodes(machine);
                nodes.add(node);
            });
            Log.warn("done.");            
        } catch (UnknownHostException ex) {
            Log.error(ex.getMessage());
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }
    }
    
}

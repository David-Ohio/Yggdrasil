/*
 * Contact: ohio@ohiotech.com.br
 * This is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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

package yggdrasil.nodes;

import br.com.ohiotech.ohiotechlogger.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Ohio
 */
public class Nodes {
    
    private static Future<Boolean> portIsOpen(final ExecutorService es,
            final String ip, final int port, final int timeout) {
        return es.submit(() -> {
            try {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                }
                return true;
            } catch (IOException ex) {
                return false;
            }
        });
    }

    final ExecutorService es = Executors.newFixedThreadPool(40);
    final int timeout = 100;
    final List<Future<Boolean>> futures = new ArrayList<>();

    public Nodes(final String ip) {
        int maxPortQuantity = 65535;
        for (int port = 1; port <= maxPortQuantity; port++) {
            futures.add(portIsOpen(es, ip, port, port));
            Log.info("Added port "+port);            
        }
        es.shutdown();
        int openPorts = 0;
        int percent = 0;
        for(final Future<Boolean> f: futures){
            try {
                if(f.get()){
                    openPorts++;
                    Log.info("Processing "+percent+"/65535 - open");
                    percent++;
                }else{
                    Log.info("Processing "+percent+"/65535 - closed");
                    percent++;
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Nodes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Log.warn("There are "+openPorts+" open ports on host "+ip+ " (probed with a timeout of "+timeout+ "ms)");
    }

}

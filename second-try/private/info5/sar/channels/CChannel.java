/*
 * Copyright (C) 2023 Pr. Olivier Gruber                                    
 *                                                                       
 * This program is free software: you can redistribute it and/or modify  
 * it under the terms of the GNU General Public License as published by  
 * the Free Software Foundation, either version 3 of the License, or     
 * (at your option) any later version.                                   
 *                                                                       
 * This program is distributed in the hope that it will be useful,       
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         
 * GNU General Public License for more details.                          
 *                                                                       
 * You should have received a copy of the GNU General Public License     
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package info5.sar.channels;

public class CChannel extends Channel {

  protected CChannel(Broker broker, int port) {
    super(broker);
    throw new RuntimeException("NYI");
  }

  // added for helping debugging applications.
  public String getRemoteName() {
    throw new RuntimeException("NYI");
  }

  @Override
  public int read(byte[] bytes, int offset, int length) {
    throw new RuntimeException("NYI");
  }

  @Override
  public int write(byte[] bytes, int offset, int length) {
    throw new RuntimeException("NYI");
  }

  @Override
  public void disconnect() {
    throw new RuntimeException("NYI");
  }

  @Override
  public boolean disconnected() {
    throw new RuntimeException("NYI");
  }

}

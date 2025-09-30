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

/*
 * Channel is a point-to-point stream of bytes.
 * Full-duplex, each end point can be used to read or write.
 * A connected channel is FIFO and lossless. 
 * A channel can be disconnected at any time, from either side.
 * 
 * Overall, this class should not be considered thread safe 
 * since the read and write operations may complete partially.
 * However, it is safe to use one task to read and one task
 * to write. It is also safe to use different tasks to operate
 * on a channel, if they properly synchronize.
 * 
 * It is safe to disconnect a channel from any task. This means
 * that a channel can be disconnect by one task while other
 * tasks are blocked in a read or write operation. These blocked
 * operations will be interrupted when appropriate. 
 */
public abstract class Channel {
  Broker broker;

  protected Channel(Broker broker) {
    this.broker = broker;
  }

  // added for helping debugging applications.
  public abstract String getRemoteName();
  
  public Broker getBroker() {
    return broker;
  }

  /*
   * Read bytes in the given array, starting at the given offset.
   * At most "length" bytes will be read. If there are no bytes available, this
   * method will block until there are bytes available or this channel
   * becomes disconnected.
   * 
   * @returns the number of bytes read, may not be zero.
   */
  public abstract int read(byte[] bytes, int offset, int length);

  /*
   * Write bytes from the given array, starting at the given
   * offset. At most "length" bytes will be written. If there is no room to write
   * any byte, this method will block until there is room or this channel
   * becomes disconnected.
   * 
   * @returns the number of bytes written, may not be zero.
   */
  public abstract int write(byte[] bytes, int offset, int length);

  /*
   * Thread-safe disconnects this channel, unblocking any task 
   * blocked in a read or write operation.
   */
  public abstract void disconnect();

  /*
   * @returns true if this channel is disconnected (thread-safe)
   */
  public abstract boolean disconnected();

}

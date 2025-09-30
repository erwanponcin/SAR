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
 * Brokers are there to permit to establish channels.
 * 
 * Each broker must be uniquely named. 
 * 
 * Each broker may be used to accept on different ports concurrently,
 * although multiple tasks would need to be used since the
 * accept is a blocking rendez-vous with a connect, on a
 * matching port.
 */
public abstract class Broker {
  String name;
  /* 
   * Each broker must be uniquely named. 
   * @throws IllegalArgumentException if the name is not unique.
   */
  protected Broker(String name) {
    this.name = name;
  }
  
  /*
   * @returns the name of this broker.
   */
  public String getName() { return name; }
  
  /*
   * Indicate that this broker will accept one connection
   * on the given port and return a fully connected channel.
   * This is a thread-safe blocking rendez-vous. 
   * @throws IllegalArgumentException if there is already 
   *         an accept pending on the given port.
   */
  public abstract Channel accept(int port);
  
  /*
   * Attempts a connection to the given port, via 
   * the broker with the given name. 
   * If such a broker cannot be found, this method returns null.
   * If the broker is found, this connect will block until
   * an accept on the given port is pending.
   * This is a thread-safe blocking rendez-vous. 
   * Note: multiple accepts from different tasks with 
   *       the same name and port are legal
   */
  public abstract Channel connect(String name, int port);
}

import { Observable } from 'rxjs';
import { GenericPage } from '../../models/generic-page';
import { Client } from '../../models/client';

export abstract class ClientsData {

    abstract getAllClients(page:number,size:number) : Observable<GenericPage<Client>>;

    abstract getClientByRFC(rfc:string) : Observable<Client>;

    abstract getClientByName(name:string) : Observable<[Client]>;

    abstract insertNewClient(client : Client) : Observable<Client>;

    abstract updateClient(client : Client) : Observable<Client>;
  
}
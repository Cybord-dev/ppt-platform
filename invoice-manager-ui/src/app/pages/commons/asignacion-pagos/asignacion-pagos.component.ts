import { Component, OnInit, Input } from '@angular/core';
import { PagoBase } from '../../../models/pago-base';
import { GenericPage } from '../../../models/generic-page';
import { Factura } from '../../../models/factura/factura';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { map } from 'rxjs/operators';
import { ClientsData } from '../../../@core/data/clients-data';
import { Client } from '../../../models/client';
import { UsersData } from '../../../@core/data/users-data';
import { User } from '../../../models/user';
import { Contribuyente } from '../../../models/contribuyente';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'ngx-asignacion-pagos',
  templateUrl: './asignacion-pagos.component.html',
  styleUrls: ['./asignacion-pagos.component.scss']
})
export class AsignacionPagosComponent implements OnInit {

  @Input() payment: PagoBase;
  public page: GenericPage<any>;
  public user: User;

  public clientsCat: Contribuyente[] = [];

  constructor(private invoiceService: InvoicesData,
    private userService: UsersData,
    protected ref: NbDialogRef<AsignacionPagosComponent>,
    private clientsService: ClientsData) {
    this.page = new GenericPage();
  }

  ngOnInit() {
    this.userService.getUserInfo().then(user => {
      this.user = user;
      this.clientsService.getClientsByPromotor(user.email)
        .pipe(
          map((clients: Client[]) => clients.map(c => c.informacionFiscal)),
        ).subscribe(clients => {
          this.clientsCat = clients;
        });
    });
  }

  public updateDataTable(currentPage?: number, pageSize?: number) {
    const pageValue = currentPage || 0;
    const sizeValue = pageSize || 10;
    this.invoiceService.getInvoices(pageValue, sizeValue, {})
      .pipe(
        map((page: GenericPage<Factura>) => {
          const content = page.content.map(r => {
            const record: any = {...r};
            record.saldo = Math.random() * r.total;
            record.saldo = (record.saldo < 10000) ? 0 : record.saldo;
            return record;
          });
          page.content = content;
          return page;
        }))
      .subscribe((result: GenericPage<any>) => this.page = result);
  }



  cancel() {
    this.ref.close();
  }

}

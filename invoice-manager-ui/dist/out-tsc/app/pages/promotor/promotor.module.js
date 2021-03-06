import * as tslib_1 from "tslib";
import { NgModule } from '@angular/core';
import { PromotorRoutingModule } from './promotor-routing.module';
import { PromotorComponent } from './promotor.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';
import { ReportesComponent } from './reportes/reportes.component';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { DownloadCsvService } from '../../@core/util-services/download-csv.service';
import { DownloadInvoiceFilesService } from '../../@core/util-services/download-invoice-files';
import { CommonsModule } from '../commons/commons.module';
let PromotorModule = class PromotorModule {
};
PromotorModule = tslib_1.__decorate([
    NgModule({
        declarations: [PromotorComponent,
            PreCfdiComponent,
            ReportesComponent,
            DevolucionesComponent],
        imports: [
            PromotorRoutingModule,
            CommonsModule,
        ],
        providers: [DownloadCsvService, DownloadInvoiceFilesService],
    })
], PromotorModule);
export { PromotorModule };
//# sourceMappingURL=promotor.module.js.map
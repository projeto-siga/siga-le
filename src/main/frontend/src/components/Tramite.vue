<template>
  <div>
    <b-modal id="cota" ref="modal" v-model="showModal" title="Tramitar" hide-header-close>
      <form>
        <div class="row">
          <div class="form-group col col-sm-6">
            <label class="control-label" for="tipo" style="width: 100%">Destino</label>
             <select class="form-control" id="tipo" v-model="tipo">
                <option value="lotacao">Lotação</option>
                <option value="matricula">Pessoa</option>
              </select>
              <span v-if="false" v-show="errors.has('tipo')" class="help is-danger">{{ errors.first('tipo') }}</span>
          </div>
          <div class="form-group col col-sm-6" v-if="tipo === 'lotacao'">
            <label class="control-label" for="lotacao" style="width: 100%">Sigla da Lotação</label>
            <b-form-input type="text" name="lotacao" id="lotacao" v-model="lotacao" class="form-control" aria-describedby="lotacaoHelp" :class="{'is-invalid': errors.has('texto') }" style="width: 100%" autofocus></b-form-input>
            <span v-if="false" v-show="errors.has('lotacao')" class="help is-danger">{{ errors.first('lotacao') }}</span>
          </div>
          <div class="form-group col col-sm-6" v-if="tipo === 'matricula'">
            <label class="control-label" for="matricula" style="width: 100%">Matrícula</label>
            <b-form-input type="matricula" name="matricula" id="matricula" v-model="matricula" class="form-control" :class="{'is-invalid': errors.has('matricula') }"></b-form-input>
            <span v-if="false" v-show="errors.has('matricula')" class="help is-danger">{{ errors.first('matricula') }}</span>
          </div>
        </div>
        <em v-if="errormsg &amp;&amp; errormsg !== ''" for="processos" class="invalid">{{errormsg}}</em>
      </form>
      <div style="width: 100%" slot="modal-footer">
        <b-btn class="float-right ml-2" variant="primary" @click="save" :disabled="errors.any()">
          Prosseguir
        </b-btn>
        <b-btn class="float-right" variant="secondary" @click="$refs.modal.hide(false)">
          Cancelar
        </b-btn>
      </div>
    </b-modal>
  </div>
</template>

<script>
import { Bus } from '../bl/bus.js'

export default {
  name: 'tramite',

  mounted () {
  },

  data () {
    return {
      showModal: false,
      errormsg: undefined,
      tipo: 'lotacao',
      lotacao: undefined,
      matricula: undefined,
      documentos: undefined
    }
  },

  methods: {
    show: function (documentos, cont) {
      this.showModal = true
      this.errormsg = undefined
      this.documentos = documentos
      this.cont = cont
    },

    cancel: function (e) {
      e.cancel()
    },

    save: function () {
      if (this.tipo === 'lotacao') {
        this.matricula = undefined
        if ((this.lotacao || '') === '') {
          this.errormsg = 'Lotação deve ser informada.'
          return
        }
      }
      if (this.tipo === 'matricula') {
        this.lotacao = undefined
        if ((this.matricula || '') === '') {
          this.errormsg = 'Matrícula deve ser informada.'
          return
        }
      }

      Bus.$emit('tramitar', this.documentos, this.lotacao, this.matricula, this.cont)
      this.$refs.modal.hide(true)
    },

    validar: function () {
      this.$nextTick(() => this.$validator.validateAll())
    }
  },

  components: {
//    'pdf-preview': PdfPreview
  }
}
</script>

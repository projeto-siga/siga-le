<template>
  <div>
    <b-modal id="cota" ref="modal" v-model="showModal" title="Assinatura com Senha" hide-header-close>
      <form>
        <div class="row">
          <div class="form-group col col-sm-6">
            <label class="control-label" for="username" style="width: 100%">Matrícula</label>
            <b-form-input type="text" name="username" id="username-sign" v-model="username" class="form-control" aria-describedby="usernameHelp" :class="{'is-invalid': errors.has('texto') }" style="width: 100%" v-validate.initial="'required'"></b-form-input>
            <span v-if="false" v-show="errors.has('username')" class="help is-danger">{{ errors.first('username') }}</span>
          </div>
          <div class="form-group col col-sm-6">
            <label class="control-label" for="password" style="width: 100%">Senha</label>
            <b-form-input type="password" name="password" id="password-sign" v-model="password" class="form-control" :class="{'is-invalid': errors.has('password') }" v-validate.initial="'required'"></b-form-input>
            <span v-if="false" v-show="errors.has('password')" class="help is-danger">{{ errors.first('password') }}</span>
          </div>
        </div>
        <small id="usernameHelp" class="form-text text-muted">
          <strong>Atenção</strong>! Ao clicar em prosseguir, será realizada a assinatura com senha. Por favor, tenha certeza que deseja realmente assinar antes de clicar em 'Prosseguir'.</small>
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
  name: 'assinatura',

  mounted () {
  },

  data () {
    return {
      showModal: false,
      errormsg: undefined,
      username: '',
      password: undefined,
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
      if ((this.username || '') === '') {
        this.errormsg = 'Matrícula deve ser informado.'
        return
      }

      if ((this.password || '') === '') {
        this.errormsg = 'Senha deve ser informada.'
        return
      }

      Bus.$emit('assinarComSenha', this.documentos, this.username, this.password, this.cont)
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

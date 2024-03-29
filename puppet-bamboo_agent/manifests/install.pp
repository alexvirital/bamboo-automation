# Logic for installing a Bamboo agent
# *** This type should be considered private to this module ***
define bamboo_agent::install(
  $home,
  $id = $title,
){
  $java         = shellquote($bamboo_agent::java_command)
  $jar          = shellquote($bamboo_agent::installer_jar)
  $register_url = shellquote("${bamboo_agent::server_url}/agentServer/")
  $quoted_home  = shellquote($home)

  $install_command = "${java} -Dbamboo.home=${quoted_home}\
  -jar ${jar} ${register_url} install"

  exec { "install-agent-${id}":
    path      => ['/bin','/usr/bin','/usr/local/bin'],
    user      => $bamboo_agent::user_name,
    group     => $bamboo_agent::user_group,
    creates   => "${home}/bin/bamboo-agent.sh",
    command   => $install_command,
    require   => File[$bamboo_agent::installer_jar],
    logoutput => true,
  }
}
